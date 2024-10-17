package com.consubanco.usecase.file.helpers;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.message.FileMessage;
import com.consubanco.model.entities.file.util.FileFactoryUtil;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.monoBusiness;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.*;
import static com.consubanco.model.entities.file.message.FileMessage.attachmentsNotFound;

@RequiredArgsConstructor
public class FileHelper {

    private final FileRepository fileRepository;
    private final PdfConvertHelper pdfConvertHelper;

    public Mono<List<File>> filesByOfferWithoutUrls(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolderWithoutUrls)
                .collectList()
                .switchIfEmpty(monoBusiness(FILES_NOT_FOUND, FileMessage.offerFilesNotFound(offerId)));
    }

    public Mono<File> save(File file) {
        return fileRepository.save(file);
    }

    public Flux<File> uploadFilesInParallel(List<FileUploadVO> fileUploadVOList, String directory) {
        return Flux.fromIterable(fileUploadVOList)
                .map(fileUploadVO ->  FileFactoryUtil.buildFromFileUploadVO(fileUploadVO, directory))
                .flatMap(fileRepository::save)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .sequential();
    }

    public Mono<Void> delete(File file) {
        return fileRepository.delete(file);
    }

    public Mono<File> findByName(String name) {
        return fileRepository.getByNameWithoutSignedUrl(name)
                .switchIfEmpty(monoBusiness(FILES_NOT_FOUND, FileMessage.notFoundByName(name)));
    }

    public Mono<File> uploadConvertedPDFAttachment(String offerId, FileUploadVO fileVo) {
        return pdfConvertHelper.convertAttachmentToPDF(fileVo)
                .map(pdfContent -> FileFactoryUtil.buildAttachmentPDF(fileVo.getName(), pdfContent, offerId))
                .flatMap(this::save);
    }

    public Mono<File> uploadAttachment(String offerId, FileUploadVO fileVo) {
        File file = FileFactoryUtil.attachmentFromFileUploadVO(fileVo, offerId);
        return this.save(file);
    }

    public Flux<File> getOfferAttachmentsWithoutUrls(String offerId) {
        return checkOfferId(offerId)
                .map(FileConstants::attachmentsDirectory)
                .flatMapMany(fileRepository::listByFolderWithoutUrls)
                .switchIfEmpty(ExceptionFactory.monoBusiness(ATTACHMENTS_NOT_FOUND, attachmentsNotFound(offerId)));
    }

    public Flux<File> getOfferAttachmentsWithUrls(String offerId) {
        String directory = FileConstants.attachmentsDirectory(offerId);
        return fileRepository.listByFolderWithUrls(directory);
    }

    public Mono<File> getPayloadTemplate() {
        return fileRepository.getPayloadTemplateWithoutSignedUrl();
    }

    private Mono<String> checkOfferId(String offerId) {
        return Mono.justOrEmpty(offerId)
                .switchIfEmpty(ExceptionFactory.buildBusiness(OFFER_ID_IS_NULL));
    }

}
