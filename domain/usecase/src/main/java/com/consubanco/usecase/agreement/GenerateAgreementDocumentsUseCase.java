package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.document.usecase.BuildAllAgreementDocumentsUseCase;
import com.consubanco.usecase.file.helpers.FileHelper;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GenerateAgreementDocumentsUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileHelper fileHelper;
    private final BuildAllAgreementDocumentsUseCase buildAllAgreementDocuments;

    public Mono<Void> execute(String processId, FileUploadVO fileSignature) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> uploadSignatureToStorage(fileSignature, process).thenReturn(process))
                .flatMap(buildAllAgreementDocuments::execute);
    }

    private Mono<File> uploadSignatureToStorage(FileUploadVO fileSignature, Process process) {
        return fileHelper.uploadAttachment(process.getOfferId(), fileSignature.toBuilder()
                .name(DocumentNames.CUSTOMER_SIGNATURE)
                .build());
    }

}
