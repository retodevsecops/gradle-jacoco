package com.consubanco.usecase.file;

import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetCustomerVisibleFilesUseCase {

    private final AgreementConfigRepository agreementConfigRepository;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final GetFilesByOfferUseCase getFilesByOfferUseCase;

    public Flux<File> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMapMany(this::processAgreementConfig);
    }

    private Flux<File> processAgreementConfig(Process process) {
        return agreementConfigRepository.getConfigByAgreement(process.getAgreementNumber())
                .flatMapMany(agreementConfigVO -> getFiles(agreementConfigVO, process.getOffer().getId()));
    }

    private Flux<File> getFiles(AgreementConfigVO agreementConfigVO, String offerId) {
        return getFilesByOfferUseCase.execute(offerId)
                .filter(file -> isFileVisible(agreementConfigVO, file));
    }

    private boolean isFileVisible(AgreementConfigVO agreementConfigVO, File file) {
        return agreementConfigVO.getCustomerVisibleDocuments().contains(file.getName());
    }

}
