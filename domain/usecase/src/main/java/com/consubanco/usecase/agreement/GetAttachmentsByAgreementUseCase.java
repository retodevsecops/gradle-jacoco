package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.document.vo.PreviousDocumentVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
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

    public Flux<AttachmentConfigVO> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMapMany(process -> agreementConfigRepository.getConfigByAgreement(process.getAgreementNumber())
                        .map(AgreementConfigVO::attachments)
                        .flatMapMany(attachments -> filterAttachments(process, attachments)));
    }

    private Flux<AttachmentConfigVO> filterAttachments(Process process, List<AttachmentConfigVO> attachments) {
        List<AttachmentConfigVO> attachmentsToRetrieved = attachmentsToRetrieved(attachments);
        if (attachmentsToRetrieved.isEmpty()) {
            return Flux.fromIterable(attachments);
        }
        return retrievePreviousDocuments(process, attachmentsToRetrieved)
                .collectList()
                .flatMapMany(list -> Flux.fromIterable(attachments)
                        .filter(attachConfig -> isNotRecoveredFile(list, attachConfig.getTechnicalName())));
    }

    private List<AttachmentConfigVO> attachmentsToRetrieved(List<AttachmentConfigVO> attachments) {
        return attachments.stream()
                .filter(AttachmentConfigVO::getIsRecoverable)
                .toList();
    }

    private Flux<File> retrievePreviousDocuments(Process process, List<AttachmentConfigVO> attachmentsToRetrieved) {
        return documentGateway.getDocsFromPreviousApplication(process.getPreviousApplicationId(), attachmentsToRetrieved)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(prevDocument -> {
                    Optional<AttachmentConfigVO> attachConfig = getAttachment(attachmentsToRetrieved, prevDocument.getName());
                    return buildFile(process, prevDocument, attachConfig.get());
                })
                .flatMap(fileRepository::save)
                .sequential();
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

    private static File buildFile(Process process, PreviousDocumentVO prevDocument, AttachmentConfigVO attachConfig) {
        return File.builder()
                .name(attachConfig.getTechnicalName())
                .content(prevDocument.getContent())
                .extension(prevDocument.getExtension())
                .directoryPath(attachmentsDirectory(process.getOffer().getId()))
                .build();
    }

}
