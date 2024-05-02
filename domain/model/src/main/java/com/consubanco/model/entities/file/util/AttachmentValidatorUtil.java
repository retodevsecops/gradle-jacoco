package com.consubanco.model.entities.file.util;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
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

    public Mono<List<FileUploadVO>> checkAttachments(List<AttachmentConfigVO> attachmentsByAgreement, List<FileUploadVO> attachmentsProvided) {
        List<String> attachmentNames = attachmentNames(attachmentsByAgreement);
        List<FileUploadVO> filteredAttachments = filteredAttachments(attachmentNames, attachmentsProvided);
        List<String> providedAttachmentNames = providedAttachmentNames(filteredAttachments);
        return checkRequiredAttachments(attachmentsByAgreement, providedAttachmentNames)
                .then(checkValidTypes(filteredAttachments, attachmentsByAgreement))
                .thenReturn(filteredAttachments);
    }

    private List<String> attachmentNames(List<AttachmentConfigVO> attachmentsByAgreement) {
        return attachmentsByAgreement.stream()
                .map(AttachmentConfigVO::getTechnicalName)
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

    private Mono<Void> checkRequiredAttachments(List<AttachmentConfigVO> attachmentsByAgreement,
                                                List<String> providedAttachmentNames) {
        return Flux.fromIterable(attachmentsByAgreement)
                .filter(AttachmentConfigVO::shouldBeValidated)
                .map(AttachmentConfigVO::getTechnicalName)
                .filter(attachmentRequired -> !providedAttachmentNames.contains(attachmentRequired))
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    throw ExceptionFactory.buildBusiness((String.join(", ", list)), MISSING_ATTACHMENT);
                })
                .then();
    }

    private Mono<Void> checkValidTypes(List<FileUploadVO> attachmentsProvided,
                                       List<AttachmentConfigVO> attachmentsByAgreement) {
        return Flux.fromIterable(attachmentsProvided)
                .filter(fileUploadVO -> {
                    List<String> validTypes = getValidTypesByFile(attachmentsByAgreement, fileUploadVO);
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

    private List<String> getValidTypesByFile(List<AttachmentConfigVO> attachmentsByAgreement, FileUploadVO fileUpload) {
        return attachmentsByAgreement.stream()
                .filter(attachment -> attachment.getTechnicalName().equalsIgnoreCase(fileUpload.getName()))
                .flatMap(attachment -> attachment.getTypeFile().stream())
                .map(String::toLowerCase)
                .toList();
    }

}
