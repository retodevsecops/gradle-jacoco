package com.consubanco.usecase.file;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.util.AttachmentValidatorUtil;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.model.entities.file.message.FileBusinessMessage.*;

@RequiredArgsConstructor
public class FileUseCase {

    private final FileRepository fileRepository;

    public Mono<File> loadPayloadTemplate() {
        return fileRepository.loadPayloadTemplate()
                .flatMap(file -> fileRepository.getPayloadTemplateWithoutSignedUrl());
    }

    public Mono<File> uploadPayloadTemplate(FileUploadVO fileUploadVO) {
        return checkFileSize(fileUploadVO)
                .filter(vo -> vo.getExtension().equalsIgnoreCase(FileExtensions.FTL))
                .switchIfEmpty(ExceptionFactory.buildBusiness(FILE_NOT_FTL))
                .flatMap(fileRepository::uploadPayloadTemplate)
                .switchIfEmpty(ExceptionFactory.buildBusiness(TEMPLATE_INCORRECT))
                .flatMap(file -> fileRepository.getPayloadTemplate());
    }

    public Mono<File> uploadAgreementsConfig(FileUploadVO fileUploadVO) {
        return checkFileSize(fileUploadVO)
                .map(FileUploadVO::getExtension)
                .filter(extension -> extension.equalsIgnoreCase(FileExtensions.JSON))
                .switchIfEmpty(ExceptionFactory.buildBusiness(FILE_NOT_JSON))
                .map(extension -> new File(fileUploadVO.getContent(), extension))
                .flatMap(fileRepository::uploadAgreementsConfigFile);
    }

    public Mono<File> uploadCreateApplicationTemplate(FileUploadVO fileUploadVO) {
        return checkFileSize(fileUploadVO)
                .filter(vo -> vo.getExtension().equalsIgnoreCase(FileExtensions.FTL))
                .switchIfEmpty(ExceptionFactory.buildBusiness(FILE_NOT_FTL))
                .flatMap(fileRepository::uploadCreateApplicationTemplate)
                .switchIfEmpty(ExceptionFactory.buildBusiness(TEMPLATE_INCORRECT))
                .flatMap(file -> fileRepository.getCreateApplicationTemplate());
    }

    public Flux<File> getManagementFiles() {
        return fileRepository.listByFolderWithUrls(FileConstants.MANAGEMENT_DIRECTORY_PATH);
    }

    public Mono<Map> validateTemplate(String template, Map<String, Object> data) {
        return fileRepository.validateTemplate(template, data);
    }

    public Mono<File> loadCreateApplicationTemplate() {
        return fileRepository.loadCreateApplicationTemplate()
                .flatMap(file -> fileRepository.getCreateApplicationTemplateWithoutSignedUrl());
    }

    private Mono<FileUploadVO> checkFileSize(FileUploadVO fileUploadVO) {
        return AttachmentValidatorUtil.checkFileSize(fileUploadVO, fileRepository.getMaxSizeOfFileInMBAllowed());
    }

}
