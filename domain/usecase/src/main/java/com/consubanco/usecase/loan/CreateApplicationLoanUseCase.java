package com.consubanco.usecase.loan;

import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Map;

@RequiredArgsConstructor
public class CreateApplicationLoanUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileRepository fileRepository;
    private final PayloadDocumentGateway payloadDocGateway;
    private final LoanGateway loanGateway;

    public Mono<Map<String, Object>> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> Mono.zip(getCreateApplicationTemplate(), payloadDocGateway.getAllData(processId)))
                .flatMap(TupleUtils.function(loanGateway::createApplication));
    }

    private Mono<String> getCreateApplicationTemplate() {
        return fileRepository.getCreateApplicationTemplate()
                .map(File::getContent);
    }
}
