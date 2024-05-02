package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class GetAttachmentsByAgreementUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final AgreementConfigRepository agreementConfigRepository;

    public Flux<AttachmentConfigVO> execute(String processId) {
        // TODO: se debe trabajar en el llamado al api para saber que documentos se pueden obtener
        //  de la solicitud anterior y luego obtener cada documento
        return getProcessByIdUseCase.execute(processId)
                .doOnNext(e -> System.out.println(e.toString()))
                .map(Process::getAgreementNumber)
                .flatMap(agreementConfigRepository::getConfigByAgreement)
                .map(AgreementConfigVO::getAttachmentsDocuments)
                .flatMapMany(Flux::fromIterable);
    }

    private Flux<AttachmentConfigVO> prev(List<AttachmentConfigVO> attachments){
        return Flux.fromIterable(attachments)
                .filter(AttachmentConfigVO::getIsRecoverable);
    }

}
