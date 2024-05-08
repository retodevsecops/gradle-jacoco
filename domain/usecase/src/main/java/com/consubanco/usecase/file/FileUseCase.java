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

import static com.consubanco.model.entities.file.message.FileBusinessMessage.*;

@RequiredArgsConstructor
public class FileUseCase {

    private final FileRepository fileRepository;

    public Mono<File> loadPayloadTemplate() {
        return fileRepository.getPayloadTemplate()
                .switchIfEmpty(fileRepository.getLocalPayloadTemplate()
                        .switchIfEmpty(ExceptionFactory.buildBusiness(PAYLOAD_TEMPLATE_NOT_FOUND))
                        .flatMap(this::uploadPayloadTemplate));
    }

    public Mono<File> uploadPayloadTemplate(FileUploadVO fileUploadVO) {
        return checkFileSize(fileUploadVO)
                .filter(vo -> vo.getExtension().equalsIgnoreCase(FileExtensions.FTL))
                .switchIfEmpty(ExceptionFactory.buildBusiness(FILE_NOT_FTL))
                .flatMap(fileRepository::uploadPayloadTemplate)
                .switchIfEmpty(ExceptionFactory.buildBusiness(PAYLOAD_TEMPLATE_INCORRECT))
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

    public Flux<File> getManagementFiles() {
        return fileRepository.listByFolder(FileConstants.MANAGEMENT_DIRECTORY_PATH);
    }

    private Mono<FileUploadVO> checkFileSize(FileUploadVO fileUploadVO) {
        return AttachmentValidatorUtil.checkFileSize(fileUploadVO, fileRepository.getMaxSizeOfFileInMBAllowed());
    }


}
