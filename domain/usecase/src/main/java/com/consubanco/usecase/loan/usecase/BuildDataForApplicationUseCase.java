package com.consubanco.usecase.loan.usecase;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileWithStorageRouteVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import com.consubanco.usecase.document.usecase.GenerateNom151UseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.FILES_NOT_FOUND;
import static com.consubanco.model.entities.loan.message.LoanMessage.requiredFiles;

@RequiredArgsConstructor
public class BuildDataForApplicationUseCase {

    private static final String FILES_KEY = "files_data";

    private final FileRepository fileRepository;
    private final GenerateNom151UseCase generateNom151UseCase;
    private final PayloadDocumentGateway payloadDocGateway;
    private final GetAgreementConfigUseCase getAgreementConfigUseCase;

    public Mono<Map<String, Object>> execute(Process process) {
        Mono<AgreementConfigVO> agreementConfigVO = getAgreementConfigUseCase.execute(process.getAgreementNumber());
        Mono<List<FileWithStorageRouteVO>> allFiles = getAllFiles(process);
        return Mono.zip(agreementConfigVO, allFiles)
                .flatMap(tuple -> getData(process, tuple.getT1(), tuple.getT2()));

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

    private Mono<Map<String, Object>> getData(Process process, AgreementConfigVO config, List<FileWithStorageRouteVO> files) {
        return payloadDocGateway.getAllData(process.getId(), config)
                .map(data -> addFilesToData(data, files));
    }

    private Map<String, Object> addFilesToData(Map<String, Object> data, List<FileWithStorageRouteVO> files) {
        data.put(FILES_KEY, files);
        return data;
    }

}
