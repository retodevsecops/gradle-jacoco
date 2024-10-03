package com.consubanco.usecase.ocr.usecase;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.util.FileFactoryUtil;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.vo.OcrDocumentSaveVO;
import com.consubanco.model.entities.ocr.vo.OcrResulSetVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.file.helpers.FileHelper;
import com.consubanco.usecase.file.helpers.PdfConvertHelper;
import com.consubanco.usecase.ocr.helpers.ValidateOcrDocumentsHelper;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.consubanco.model.entities.file.constant.FileConstants.attachmentsDirectory;
import static reactor.core.publisher.Mono.just;

@RequiredArgsConstructor
public class ValidateOcrDocumentUseCase {

    private final GetProcessByIdUseCase getProcessById;
    private final FileHelper fileHelper;
    private final PdfConvertHelper pdfConvertHelper;
    private final OcrDocumentGateway ocrDocumentGateway;
    private final ValidateOcrDocumentsHelper validateOcrDocuments;
    private final OcrDocumentRepository ocrDocumentRepository;

    public Mono<OcrResulSetVO> execute(String processId, FileUploadVO fileUploadVO, boolean applyOcr) {
        return getProcessById.execute(processId)
                .flatMap(process -> buildFile(fileUploadVO, process.getOfferId())
                        .flatMap(fileHelper::save)
                        .flatMap(file -> applyOcr ? processOcrFile(process, file) : just(new OcrResulSetVO(file))));
    }

    private Mono<OcrResulSetVO> processOcrFile(Process process, File file) {
        return notifyDocument(process, file)
                .map(List::of)
                .flatMap(validateOcrDocuments::execute)
                .flatMapMany(Flux::fromIterable)
                .next()
                .flatMap(ocrDocument -> buildResponseAndDeleteOnFailure(file, ocrDocument));
    }

    private Mono<File> buildFile(FileUploadVO fileUploadVO, String offerId) {
        String directory = attachmentsDirectory(offerId);
        return pdfConvertHelper.convertAttachmentToPDF(fileUploadVO)
                .map(content -> FileFactoryUtil.buildPDF(fileUploadVO.getName(), content, directory));
    }

    private Mono<OcrDocument> notifyDocument(Process process, File file) {
        OcrDocumentType ocrDocumentType = OcrDocumentType.getTypeFromName(file.getName());
        return ocrDocumentGateway.notifyDocumentForAnalysis(file.getStorageRoute(), ocrDocumentType)
                .map(analysisId -> buildOcrDocumentSave(process, file, analysisId))
                .flatMap(ocrDocumentRepository::save);

    }

    private OcrDocumentSaveVO buildOcrDocumentSave(Process process, File file, String analysisId) {
        return OcrDocumentSaveVO.builder()
                .name(file.getName())
                .storageId(file.getId())
                .storageRoute(file.getStorageRoute())
                .processId(process.getId())
                .analysisId(analysisId)
                .status(OcrStatus.PENDING)
                .build();
    }

    private Mono<OcrResulSetVO> buildResponseAndDeleteOnFailure(File file, OcrDocument ocrDocument) {
        return Mono.just(ocrDocument)
                .filter(ocr -> Objects.isNull(ocr.getFailureCode()))
                .switchIfEmpty(fileHelper.delete(file).then(Mono.empty()))
                .thenReturn(new OcrResulSetVO(file, ocrDocument));
    }

}
