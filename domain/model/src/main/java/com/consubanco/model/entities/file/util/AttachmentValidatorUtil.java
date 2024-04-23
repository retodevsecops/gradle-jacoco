package com.consubanco.model.entities.file.util;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.consubanco.model.entities.file.message.FileBusinessMessage.*;

@UtilityClass
public class AttachmentValidatorUtil {

    public Mono<Void> checkAttachmentsSize(List<FileUploadVO> attachments, Double maxSizeAllowed) {
        return Flux.fromIterable(attachments)
                .filter(fileUploadVO -> fileUploadVO.getSizeInMB() > maxSizeAllowed)
                .map(FileUploadVO::getName)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    throw ExceptionFactory.buildBusiness((String.join(", ", list)), ATTACHMENT_INVALID_SIZE);
                })
                .then();
    }

    public Mono<Void> checkAttachments(List<Agreement.Document> attachmentsRequired, List<FileUploadVO> attachmentsProvided) {
        return checkRequiredAttachments(attachmentsRequired, attachmentsProvided)
                .then(checkValidTypes(attachmentsProvided, attachmentsRequired));
    }

    private Mono<Void> checkRequiredAttachments(List<Agreement.Document> attachmentsRequired,
                                                List<FileUploadVO> attachmentsProvided) {
        List<String> namesAttachmentsProvided = extractAttachmentNames(attachmentsProvided);
        return Flux.fromIterable(attachmentsRequired)
                .filter(Agreement.Document::getIsRequired)
                .map(Agreement.Document::getTechnicalName)
                .filter(attachmentRequired -> !namesAttachmentsProvided.contains(attachmentRequired))
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    throw ExceptionFactory.buildBusiness((String.join(", ", list)), MISSING_ATTACHMENT);
                })
                .then();
    }

    private Mono<Void> checkValidTypes(List<FileUploadVO> attachmentsProvided,
                                       List<Agreement.Document> attachmentsRequired) {
        return Flux.fromIterable(attachmentsProvided)
                .filter(fileUploadVO -> {
                    List<String> validTypes = getValidTypesByFile(attachmentsRequired, fileUploadVO);
                    return !validTypes.contains(fileUploadVO.getExtension().toLowerCase());
                })
                .map(FileUploadVO::getName)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    throw ExceptionFactory.buildBusiness((String.join(", ", list)), ATTACHMENT_INVALID_TYPE);
                })
                .then();

    }

    private List<String> getValidTypesByFile(List<Agreement.Document> attachmentsRequired, FileUploadVO fileUpload) {
        return attachmentsRequired.stream()
                .filter(document -> document.getTechnicalName().equalsIgnoreCase(fileUpload.getName()))
                .flatMap(document -> document.getTypeFile().stream())
                .map(String::toLowerCase)
                .toList();
    }

    private List<String> extractAttachmentNames(List<FileUploadVO> attachments) {
        return attachments.stream()
                .map(FileUploadVO::getName)
                .collect(Collectors.toList());
    }

}
