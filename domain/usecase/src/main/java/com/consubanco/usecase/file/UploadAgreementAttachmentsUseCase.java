package com.consubanco.usecase.file;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.util.FileFactoryUtil;
import com.consubanco.model.entities.file.vo.AttachmentFileVO;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.model.entities.file.vo.FileWithStorageRouteVO;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import com.consubanco.usecase.file.helpers.PdfConvertHelper;
import com.consubanco.usecase.ocr.usecase.ProcessOcrAttachmentsUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.CNCA_NOT_FOUND;
import static com.consubanco.model.entities.document.message.DocumentMessage.GENERATE_CNCA;
import static com.consubanco.model.entities.file.constant.FileConstants.attachmentsDirectory;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkAttachments;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkAttachmentsSize;

@RequiredArgsConstructor
public class UploadAgreementAttachmentsUseCase {

    private final AgreementGateway agreementGateway;
    private final GetAgreementConfigUseCase getAgreementConfigUseCase;
    private final FileRepository fileRepository;
    private final PdfConvertHelper pdfConvertHelper;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final ProcessOcrAttachmentsUseCase processOcrAttachments;

    public Mono<List<OcrDocument>> execute(String processId, List<AttachmentFileVO> attachments) {
        return checkAttachmentsSize(attachments, fileRepository.getMaxSizeOfFileInMBAllowed())
                .then(getProcessByIdUseCase.execute(processId))
                .flatMap(process -> startProcess(attachments, process));
    }

    private Mono<List<OcrDocument>> startProcess(List<AttachmentFileVO> attachments, Process process) {
        Mono<Agreement> agreement = agreementGateway.findByNumber(process.getAgreementNumber());
        Mono<AgreementConfigVO> agreementConfig = getAgreementConfigUseCase.execute(process.getAgreementNumber());
        Mono<List<String>> filesInStorage = filesByOffer(process.getOfferId());
        return Mono.zip(agreement, agreementConfig, filesInStorage)
                .flatMap(tuple -> Mono.fromCallable(() -> validateCncaLetter(tuple.getT3()))
                        .then(checkAttachments(tuple.getT2().getAttachmentsDocuments(), attachments, tuple.getT3()))
                        .flatMap(validAttachments -> uploadAttachments(validAttachments, process.getOfferId()))
                        .flatMap(list -> processOcrAttachments.execute(process, tuple.getT2(), list)));
    }

    private Mono<List<String>> filesByOffer(String offerId) {
        return fileRepository.listByFolder(FileConstants.offerDirectory(offerId))
                .map(FileWithStorageRouteVO::getName)
                .collectList();
    }

    private String validateCncaLetter(List<String> filesInStorage) {
        return filesInStorage.stream()
                .filter(fileName -> fileName.equalsIgnoreCase(DocumentNames.CNCA_LETTER))
                .findFirst()
                .orElseThrow(() -> ExceptionFactory.buildBusiness(GENERATE_CNCA, CNCA_NOT_FOUND));
    }

    private Mono<List<File>> uploadAttachments(List<AttachmentFileVO> attachments, String offerId) {
        return buildAttachmentList(attachments, offerId)
                .parallel()
                .runOn(Schedulers.parallel())
                .concatMap(fileRepository::save)
                .sequential()
                .collectList();
    }

    private Flux<File> buildAttachmentList(List<AttachmentFileVO> attachments, String offerId) {
        return Flux.fromIterable(attachments)
                .flatMap(attachment -> {
                    if (attachment.getFiles().size() > 1) return processSingleFiles(offerId, attachment);
                    return buildMergedFile(offerId, attachment).flux();
                });
    }

    private Flux<File> processSingleFiles(String offerId, AttachmentFileVO attachmentFileVO) {
        return buildSingleFiles(offerId, attachmentFileVO)
                .flatMapMany(Flux::fromIterable)
                .concatWith(buildMergedFile(offerId, attachmentFileVO).flux());
    }

    private Mono<File> buildMergedFile(String offerId, AttachmentFileVO attachmentFileVO) {
        return Flux.fromIterable(attachmentFileVO.getFiles())
                .flatMap(pdfConvertHelper::convertAttachmentToPDF)
                .collectList()
                .flatMap(pdfConvertHelper::merge)
                .map(mergedDocument -> buildFile(offerId, attachmentFileVO.getName(), mergedDocument));
    }

    private Mono<List<File>> buildSingleFiles(String offerId, AttachmentFileVO attachmentFileVO) {
        return Flux.fromIterable(attachmentFileVO.getFiles())
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(fileUploadVO -> buildFile(fileUploadVO, offerId))
                .sequential()
                .collectList();
    }

    private Mono<File> buildFile(FileUploadVO fileUploadVO, String offerId) {
        return pdfConvertHelper.convertAttachmentToPDF(fileUploadVO)
                .map(pdfContent -> buildFile(offerId, fileUploadVO.getName(), pdfContent));
    }

    private static File buildFile(String offerId, String name, String content) {
        String directory = attachmentsDirectory(offerId);
        return FileFactoryUtil.buildPDF(name, content, directory);
    }

}
