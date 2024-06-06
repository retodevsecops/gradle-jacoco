package com.consubanco.model.entities.file.util;

import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildBusiness;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.monoBusiness;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.*;
import static com.consubanco.model.entities.file.message.FileMessage.maxSize;

@UtilityClass
public class AttachmentValidatorUtil {

    private static final String DELIMITER = ", ";

    public Mono<Void> checkAttachmentsSize(List<FileUploadVO> attachments, Double maxSizeAllowed) {
        return Flux.fromIterable(attachments)
                .filter(fileUploadVO -> fileUploadVO.getSizeInMB() > maxSizeAllowed)
                .map(FileUploadVO::getName)
                .collectList()
                .filter(list -> !list.isEmpty())
                .handle((list, sink) -> sink.error(buildBusiness((String.join(", ", list)), ATTACHMENT_INVALID_SIZE)))
                .then();
    }

    public Mono<List<FileUploadVO>> checkAttachments(List<AttachmentConfigVO> attachmentsByAgreement,
                                                     List<FileUploadVO> attachmentsProvided,
                                                     List<String> attachmentsInStorage) {
        List<String> attachmentNamesByAgreement = attachmentNames(attachmentsByAgreement);
        List<FileUploadVO> filteredAttachments = filteredAttachments(attachmentNamesByAgreement, attachmentsProvided);
        List<String> providedAttachmentNames = providedAttachmentNames(filteredAttachments);
        return checkRequiredAttachments(attachmentsByAgreement, providedAttachmentNames, attachmentsInStorage)
                .then(checkValidTypes(filteredAttachments, attachmentsByAgreement))
                .thenReturn(filteredAttachments);
    }

    public Mono<FileUploadVO> checkFileSize(FileUploadVO vo, Double  maxSizeAllowed) {
        if (vo.getSizeInMB() <= 0) return buildBusiness(MIN_INVALID_SIZE);
        if (vo.getSizeInMB() > maxSizeAllowed ) return monoBusiness(ATTACHMENT_INVALID_SIZE, maxSize(maxSizeAllowed));
        return Mono.just(vo);
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
                                                List<String> attachmentsProvided,
                                                List<String> attachmentsInStorage) {
        return Flux.fromIterable(attachmentsByAgreement)
                .filter(AttachmentConfigVO::shouldBeValidated)
                .map(AttachmentConfigVO::getTechnicalName)
                .filter(attachment -> !attachmentsProvided.contains(attachment) && !attachmentsInStorage.contains(attachment))
                .collectList()
                .filter(list -> !list.isEmpty())
                .handle((list, sink) -> sink.error(buildBusiness((String.join(DELIMITER, list)), MISSING_ATTACHMENT)))
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
                .handle((list, sink) -> sink.error(buildBusiness((String.join(", ", list)), ATTACHMENT_INVALID_TYPE)))
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
