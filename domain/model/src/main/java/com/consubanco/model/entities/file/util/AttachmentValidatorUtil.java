package com.consubanco.model.entities.file.util;

import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.file.vo.AttachmentFileVO;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildBusiness;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.monoBusiness;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.*;
import static com.consubanco.model.entities.file.message.FileMessage.*;

@UtilityClass
public class AttachmentValidatorUtil {

    public Mono<Void> checkAttachmentsSize(List<AttachmentFileVO> attachments, Double sizeAllowed) {
        return flattenAttachmentFiles(attachments)
                .filter(fileUploadVO -> fileUploadVO.getSizeInMB() > sizeAllowed)
                .map(FileUploadVO::getName)
                .collectList()
                .filter(list -> !list.isEmpty())
                .handle((list, sink) -> sink.error(buildBusiness(filesExceedSize(list, sizeAllowed), ATTACHMENT_INVALID_SIZE)))
                .then();
    }

    public Mono<List<AttachmentFileVO>> checkAttachments(List<AttachmentConfigVO> attachmentsByAgreement,
                                                         List<AttachmentFileVO> attachmentsProvided,
                                                         List<String> attachmentsInStorage) {
        List<String> attachmentNamesByAgreement = attachmentNames(attachmentsByAgreement);
        List<AttachmentFileVO> filteredAttachments = filteredAttachments(attachmentNamesByAgreement, attachmentsProvided);
        List<String> providedAttachmentNames = providedAttachmentNames(filteredAttachments);
        return checkRequiredAttachments(attachmentsByAgreement, providedAttachmentNames, attachmentsInStorage)
                .then(checkValidTypes(filteredAttachments, attachmentsByAgreement))
                .then(checkNumberFilesByAttachment(filteredAttachments, attachmentsByAgreement))
                .thenReturn(filteredAttachments);
    }

    public Mono<FileUploadVO> checkFileSize(FileUploadVO vo, Double maxSizeAllowed) {
        if (vo.getSizeInMB() <= 0) return buildBusiness(MIN_INVALID_SIZE);
        if (vo.getSizeInMB() > maxSizeAllowed) return monoBusiness(ATTACHMENT_INVALID_SIZE, maxSize(maxSizeAllowed));
        return Mono.just(vo);
    }

    private Flux<FileUploadVO> flattenAttachmentFiles(List<AttachmentFileVO> attachments) {
        List<FileUploadVO> list = attachments.stream()
                .flatMap(attachmentFileVO -> attachmentFileVO.getFiles().stream())
                .toList();
        return Flux.fromIterable(list);
    }

    private List<String> attachmentNames(List<AttachmentConfigVO> attachmentsByAgreement) {
        return attachmentsByAgreement.stream()
                .map(AttachmentConfigVO::getTechnicalName)
                .collect(Collectors.toList());
    }

    private List<AttachmentFileVO> filteredAttachments(List<String> requiredAttachmentNames,
                                                       List<AttachmentFileVO> attachmentsProvided) {
        return attachmentsProvided.stream()
                .filter(attachmentFileVO -> requiredAttachmentNames.contains(attachmentFileVO.getName()))
                .toList();
    }

    private List<String> providedAttachmentNames(List<AttachmentFileVO> attachmentsProvided) {
        return attachmentsProvided.stream()
                .map(AttachmentFileVO::getName)
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
                .handle((list, sink) -> sink.error(buildBusiness(attachmentRequired(list), MISSING_ATTACHMENT)))
                .then();
    }

    private Mono<Void> checkValidTypes(List<AttachmentFileVO> attachmentsProvided,
                                       List<AttachmentConfigVO> attachmentsByAgreement) {
        return Flux.fromIterable(attachmentsProvided)
                .flatMap(attachmentFileVO -> checkAttachmentFileTypes(attachmentsByAgreement, attachmentFileVO))
                .collectList()
                .filter(list -> !list.isEmpty())
                .handle((list, sink) -> sink.error(buildBusiness(filesInvalidTypes(list), ATTACHMENT_INVALID_TYPE)))
                .then();

    }

    private static Flux<String> checkAttachmentFileTypes(List<AttachmentConfigVO> attachmentsByAgreement,
                                                         AttachmentFileVO attachmentFileVO) {
        List<String> validTypes = getValidTypesByFile(attachmentsByAgreement, attachmentFileVO);
        return Flux.fromIterable(attachmentFileVO.getFiles())
                .filter(fileUploadVO -> !validTypes.contains(fileUploadVO.getExtension().toLowerCase()))
                .map(FileUploadVO::getName);
    }

    private List<String> getValidTypesByFile(List<AttachmentConfigVO> attachmentsByAgreement, AttachmentFileVO attach) {
        return attachmentsByAgreement.stream()
                .filter(attachment -> attachment.getTechnicalName().equalsIgnoreCase(attach.getName()))
                .flatMap(attachment -> attachment.getTypeFile().stream())
                .map(String::toLowerCase)
                .toList();
    }

    private Mono<Void> checkNumberFilesByAttachment(List<AttachmentFileVO> attachmentsProvided,
                                                    List<AttachmentConfigVO> attachmentsByAgreement) {
        return Flux.fromIterable(attachmentsProvided)
                .filter(attachmentFileVO -> isNumberOfFilesExceeded(attachmentsByAgreement, attachmentFileVO))
                .map(AttachmentFileVO::getName)
                .collectList()
                .filter(list -> !list.isEmpty())
                .handle((list, sink) -> sink.error(buildBusiness(attachmentExceeded(list), ATTACHMENT_EXCEEDED)))
                .then();
    }

    private Boolean isNumberOfFilesExceeded(List<AttachmentConfigVO> attachmentsByAgreement,
                                            AttachmentFileVO attachmentFileVO) {
        return attachmentsByAgreement.stream()
                .filter(config -> config.getTechnicalName().equalsIgnoreCase(attachmentFileVO.getName()))
                .findFirst()
                .map(config -> attachmentFileVO.getFiles().size() > config.getMaxFiles())
                .orElse(false);
    }

}
