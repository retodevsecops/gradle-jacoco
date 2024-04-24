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

    public Mono<List<FileUploadVO>> checkAttachments(List<Agreement.Document> attachmentsRequired, List<FileUploadVO> attachmentsProvided) {
        List<String> requiredAttachmentNames = requiredAttachmentNames(attachmentsRequired);
        List<FileUploadVO> filteredAttachments = filteredAttachments(requiredAttachmentNames, attachmentsProvided);
        List<String> providedAttachmentNames = providedAttachmentNames(filteredAttachments);
        return checkRequiredAttachments(attachmentsRequired, providedAttachmentNames)
                .then(checkValidTypes(filteredAttachments, attachmentsRequired))
                .thenReturn(filteredAttachments);
    }

    private List<String> requiredAttachmentNames(List<Agreement.Document> attachmentsRequired) {
        return attachmentsRequired.stream()
                .map(Agreement.Document::getTechnicalName)
                .collect(Collectors.toList());
    }

    private List<FileUploadVO> filteredAttachments(List<String> requiredAttachmentNames,
                                                   List<FileUploadVO> attachmentsProvided) {
        return attachmentsProvided.stream()
                .filter(uploadVO -> requiredAttachmentNames.contains(uploadVO.getName()))
                .collect(Collectors.toList());
    }

    private List<String> providedAttachmentNames(List<FileUploadVO> attachmentsProvided) {
        return attachmentsProvided.stream()
                .map(FileUploadVO::getName)
                .collect(Collectors.toList());
    }

    private Mono<Void> checkRequiredAttachments(List<Agreement.Document> attachmentsRequired,
                                                List<String> providedAttachmentNames) {
        return Flux.fromIterable(attachmentsRequired)
                .filter(Agreement.Document::getIsRequired)
                .map(Agreement.Document::getTechnicalName)
                .filter(attachmentRequired -> !providedAttachmentNames.contains(attachmentRequired))
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

}
