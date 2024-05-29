package com.consubanco.usecase.loan;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileWithStorageRouteVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.document.GenerateNom151UseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.FILES_NOT_FOUND;
import static com.consubanco.model.entities.loan.message.LoanMessage.requiredFiles;

@RequiredArgsConstructor
public class BuildDataForApplicationUseCase {

    private final FileRepository fileRepository;
    private final GenerateNom151UseCase generateNom151UseCase;
    private final PayloadDocumentGateway payloadDocGateway;

    public Mono<Map<String, Object>> execute(Process process) {
        Mono<Map<String, Object>> allDataMap = payloadDocGateway.getAllData(process.getId());
        Mono<List<FileWithStorageRouteVO>> allFiles = getAllFiles(process);
        return Mono.zip(allDataMap, allFiles)
                .map(tuple -> {
                    tuple.getT1().put("files_data", tuple.getT2());
                    return tuple.getT1();
                });

    }

    private Mono<List<FileWithStorageRouteVO>> getAllFiles(Process process) {
        return generateNom151UseCase.execute(process)
                .flatMap(files -> getOfferFiles(process));
    }

    private Mono<List<FileWithStorageRouteVO>> getOfferFiles(Process process) {
        return Mono.just(process.getOfferId())
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolder)
                .collectList()
                .filter(files -> !files.isEmpty())
                .switchIfEmpty(ExceptionFactory.monoBusiness(FILES_NOT_FOUND, requiredFiles(process.getOfferId())));
    }

}
