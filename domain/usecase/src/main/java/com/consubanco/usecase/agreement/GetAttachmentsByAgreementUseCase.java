package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.document.vo.PreviousDocumentVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.document.usecase.BuildAllAgreementDocumentsUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

import static com.consubanco.model.entities.file.constant.FileConstants.attachmentsDirectory;

@RequiredArgsConstructor
public class GetAttachmentsByAgreementUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final AgreementConfigRepository agreementConfigRepository;
    private final DocumentGateway documentGateway;
    private final FileRepository fileRepository;
    private final BuildAllAgreementDocumentsUseCase buildAllAgreementDocumentsUseCase;

    public Flux<AttachmentConfigVO> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMapMany(this::processAttachmentQuery);
    }

    private Flux<AttachmentConfigVO> processAttachmentQuery(Process process) {
        return getAttachmentByAgreement(process)
                .collectList()
                .flatMapMany(list -> checkAttachmentList(process, list));
    }

    private Flux<AttachmentConfigVO> checkAttachmentList(Process process, List<AttachmentConfigVO> list) {
        if (list.isEmpty()) return buildAllAgreementDocumentsUseCase.execute(process).thenMany(Flux.empty());
        return Flux.fromIterable(list);
    }

    private Flux<AttachmentConfigVO> getAttachmentByAgreement(Process process) {
        return agreementConfigRepository.getConfigByAgreement(process.getAgreementNumber())
                .map(AgreementConfigVO::attachments)
                .flatMapMany(attachments -> filterAttachments(process, attachments));
    }

    private Flux<AttachmentConfigVO> filterAttachments(Process process, List<AttachmentConfigVO> attachments) {
        List<AttachmentConfigVO> attachmentsToRetrieved = attachmentsToRetrieved(attachments);
        if (attachmentsToRetrieved.isEmpty()) return Flux.fromIterable(attachments);
        return retrieveDocuments(process, attachmentsToRetrieved)
                .collectList()
                .flatMapMany(list -> Flux.fromIterable(attachments)
                        .filter(attachConfig -> isNotRecoveredFile(list, attachConfig.getTechnicalName())));
    }

    private List<AttachmentConfigVO> attachmentsToRetrieved(List<AttachmentConfigVO> attachments) {
        return attachments.stream()
                .filter(AttachmentConfigVO::getIsRecoverable)
                .toList();
    }

    private Flux<File> retrieveDocuments(Process process, List<AttachmentConfigVO> attachmentsToRetrieved) {
        return getAttachmentsFromStorage(process, attachmentsToRetrieved)
                .collectList()
                .flatMapMany(attachmentsInStorage -> merge(process, attachmentsToRetrieved, attachmentsInStorage));
    }

    private Flux<File> getAttachmentsFromStorage(Process process, List<AttachmentConfigVO> attachmentsToRetrieved) {
        return fileRepository.listByFolderWithoutUrls(attachmentsDirectory(process.getOffer().getId()))
                .filter(file -> fileIsRecoverable(file, attachmentsToRetrieved));
    }

    private boolean fileIsRecoverable(File file, List<AttachmentConfigVO> attachmentsToRetrieved) {
        return attachmentsToRetrieved.stream()
                .anyMatch(vo -> vo.getTechnicalName().equalsIgnoreCase(file.getName()));
    }

    private Flux<File> merge(Process process, List<AttachmentConfigVO> attachmentsToRetrieved, List<File> attachmentsInStorage) {
        List<AttachmentConfigVO> excludeAttachments = excludeAttachments(attachmentsToRetrieved, attachmentsInStorage);
        if (excludeAttachments.isEmpty()) return Flux.fromIterable(attachmentsInStorage);
        Flux<File> previousDocuments = retrievePreviousDocuments(process, excludeAttachments);
        return Flux.merge(Flux.fromIterable(attachmentsInStorage), previousDocuments);
    }

    private List<AttachmentConfigVO> excludeAttachments(List<AttachmentConfigVO> attachmentsToRetrieved, List<File> attachmentsInStorage) {
        return attachmentsToRetrieved.stream()
                .filter(attachmentConfigVO -> !attachmentToRetrievedIsInStorage(attachmentConfigVO, attachmentsInStorage))
                .toList();
    }

    private boolean attachmentToRetrievedIsInStorage(AttachmentConfigVO attachmentToRetrieved, List<File> attachmentsInStorage) {
        return attachmentsInStorage.stream()
                .anyMatch(file -> file.getName().equalsIgnoreCase(attachmentToRetrieved.getTechnicalName()));
    }

    private Flux<File> retrievePreviousDocuments(Process process, List<AttachmentConfigVO> attachmentsToRetrieved) {
        return documentGateway.getDocsFromPreviousApplication(process.getPreviousApplicationId(), attachmentsToRetrieved)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(prevDocument -> buildAttachment(process, attachmentsToRetrieved, prevDocument))
                .flatMap(fileRepository::save)
                .sequential();
    }

    private File buildAttachment(Process process, List<AttachmentConfigVO> attachmentsToRetrieved, PreviousDocumentVO prevDocument) {
        Optional<AttachmentConfigVO> attachConfig = getAttachment(attachmentsToRetrieved, prevDocument.getName());
        return buildFile(process, prevDocument, attachConfig.get());
    }

    private boolean isNotRecoveredFile(List<File> recoveredFiles, String nameFile) {
        return recoveredFiles.stream()
                .noneMatch(file -> file.getName().equalsIgnoreCase(nameFile));
    }

    private Optional<AttachmentConfigVO> getAttachment(List<AttachmentConfigVO> attachments, String previousDocument) {
        return attachments.stream()
                .filter(attachment -> attachment.getNamePreviousDocument().equalsIgnoreCase(previousDocument))
                .findFirst();
    }

    private File buildFile(Process process, PreviousDocumentVO prevDocument, AttachmentConfigVO attachConfig) {
        return File.builder()
                .name(attachConfig.getTechnicalName())
                .content(prevDocument.getContent())
                .extension(prevDocument.getExtension())
                .directoryPath(attachmentsDirectory(process.getOffer().getId()))
                .build();
    }

}
