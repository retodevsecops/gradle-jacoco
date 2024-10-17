package com.consubanco.usecase.loan.usecase;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileStorageVO;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentsKeys;
import com.consubanco.model.entities.ocr.util.OcrDataUtil;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import com.consubanco.usecase.ocr.helpers.GetOcrAttachmentsHelper;
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
    private final PayloadDocumentGateway payloadDocGateway;
    private final GetAgreementConfigUseCase getAgreementConfigUseCase;
    private final GetOcrAttachmentsHelper getOcrHelper;

    public Mono<Map<String, Object>> execute(Process process) {
        Mono<AgreementConfigVO> agreementConfigVO = getAgreementConfigUseCase.execute(process.getAgreementNumber());
        Mono<List<FileStorageVO>> allFiles = this.getOfferFilesFromStorage(process);
        return Mono.zip(agreementConfigVO, allFiles)
                .flatMap(tuple -> getData(process, tuple.getT1(), tuple.getT2()));
    }

    private Mono<List<FileStorageVO>> getOfferFilesFromStorage(Process process) {
        return Mono.just(process.getOfferId())
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolder)
                .collectList()
                .filter(files -> !files.isEmpty())
                .switchIfEmpty(ExceptionFactory.monoBusiness(FILES_NOT_FOUND, requiredFiles(process.getOfferId())));
    }

    private Mono<Map<String, Object>> getData(Process process, AgreementConfigVO config, List<FileStorageVO> files) {
        return Mono.zip(payloadDocGateway.getAllData(process.getId(), config), getOcrHelper.execute(process, config))
                .map(dataTuple -> {
                    Map<String, Object> dataMap = dataTuple.getT1();
                    List<OcrDocument> ocrDocuments = dataTuple.getT2();
                    List<Map<String, Object>> ocrDocumentDataList = OcrDataUtil.ocrDocumentsToMapList(ocrDocuments);
                    dataMap.put(FILES_KEY, files);
                    dataMap.put(OcrDocumentsKeys.DATA, ocrDocumentDataList);
                    return dataMap;
                });
    }

}
