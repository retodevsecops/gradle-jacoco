package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class GetAttachmentsByAgreementUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final AgreementConfigRepository agreementConfigRepository;

    public Mono<List<AgreementConfigVO.Attachment>> execute(String processId) {
        // TODO: se debe trabajar en el llamado al api para saber que documentos se pueden obtener
        //  de la solicitud anterior y luego obtener cada documento
        return getProcessByIdUseCase.execute(processId)
                .map(Process::getAgreementNumber)
                .flatMap(agreementConfigRepository::getConfigByAgreement)
                .map(AgreementConfigVO::getAttachments);
    }

}
