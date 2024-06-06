package com.consubanco.usecase.document;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class BuildPayloadDocumentUseCase {

    private final FileRepository fileRepository;
    private final PayloadDocumentGateway payloadGateway;
    private final GetAgreementConfigUseCase agreementConfigUseCase;

    public Mono<Map<String, Object>> execute(Process process) {
        return Mono.zip(agreementConfigUseCase.execute(process.getAgreementNumber()), getPayloadTemplate())
                .flatMap(tuple -> buildPayload(process, tuple.getT1(), tuple.getT2()));
    }

    private Mono<Map<String, Object>> buildPayload(Process process, AgreementConfigVO agreementConfig, String template) {
        return payloadGateway.getAllData(process.getId(), agreementConfig)
                .flatMap(allData -> payloadGateway.buildPayload(template, allData));
    }

    private Mono<String> getPayloadTemplate() {
        return fileRepository.getPayloadTemplateWithoutSignedUrl()
                .map(File::getContent);
    }

}
