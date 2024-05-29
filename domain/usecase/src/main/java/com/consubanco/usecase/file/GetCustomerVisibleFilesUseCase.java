package com.consubanco.usecase.file;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.agreement.message.AgreementBusinessMessage.AGREEMENT_CONFIG_NOT_FOUND;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.FILES_NOT_FOUND;

@RequiredArgsConstructor
public class GetCustomerVisibleFilesUseCase {

    private final AgreementConfigRepository agreementConfigRepository;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileRepository fileRepository;

    public Flux<File> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMapMany(this::processAgreementConfig);
    }

    private Flux<File> processAgreementConfig(Process process) {
        return agreementConfigRepository.getConfigByAgreement(process.getAgreementNumber())
                .switchIfEmpty(ExceptionFactory.buildBusiness(AGREEMENT_CONFIG_NOT_FOUND))
                .map(AgreementConfigVO::checkCustomerVisibleDocuments)
                .flatMapMany(agreementConfigVO -> getCustomerViewableFiles(agreementConfigVO, process));
    }

    private Flux<File> getCustomerViewableFiles(AgreementConfigVO agreementConfigVO, Process process) {
        return getFilesByOffer(process.getOfferId())
                .filter(file -> isFileVisible(agreementConfigVO, file));
    }

    private Flux<File> getFilesByOffer(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolderWithUrls)
                .switchIfEmpty(ExceptionFactory.buildBusiness(FILES_NOT_FOUND));
    }

    private boolean isFileVisible(AgreementConfigVO agreementConfigVO, File file) {
        return agreementConfigVO.getCustomerVisibleDocuments().contains(file.getName());
    }

}
