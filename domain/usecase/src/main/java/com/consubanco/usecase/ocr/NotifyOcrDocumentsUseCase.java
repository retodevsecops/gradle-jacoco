package com.consubanco.usecase.ocr;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.vo.OcrDocumentSaveVO;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class NotifyOcrDocumentsUseCase {

    private static final Pattern BASE_NAME_PATTERN = Pattern.compile("^(.*?)(-\\d+)?$");
    private final OcrDocumentGateway ocrDocumentGateway;
    private final OcrDocumentRepository ocrDocumentRepository;

    public Flux<OcrDocument> execute(Process process, AgreementConfigVO agreementConfig, List<File> attachments) {
        List<String> ocrAttachments = agreementConfig.getOcrAttachmentsTechnicalNames();
        List<File> filteredFiles = removeCompoundAttachments(attachments);
        return Flux.fromIterable(filteredFiles)
                .filter(file -> ocrAttachments.contains(getBaseFileName(file.getName())))
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(file -> notifyDocument(process, file))
                .sequential()
                .collectList()
                .flatMapMany(ocrDocumentRepository::saveAll);
    }

    private List<File> removeCompoundAttachments(List<File> files) {
        return groupedFiles(files)
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().size() > 1 ? filterGroup(e.getKey(), e.getValue()) : e.getValue().stream())
                .toList();
    }

    private Map<String, List<File>> groupedFiles(List<File> files) {
        return files.stream()
                .collect(Collectors.groupingBy(file -> getBaseFileName(file.getName())));
    }

    private String getBaseFileName(String fileName) {
        Matcher matcher = BASE_NAME_PATTERN.matcher(fileName);
        return matcher.matches() ? matcher.group(1) : fileName;
    }

    private static Stream<File> filterGroup(String key, List<File> group) {
        return group.stream().filter(file -> !file.getName().equals(key));
    }

    private Mono<OcrDocumentSaveVO> notifyDocument(Process process, File file) {
        OcrDocumentType ocrDocumentType = OcrDocumentType.getTypeFromName(file.getName());
        return ocrDocumentGateway.notifyDocumentForAnalysis(file.getStorageRoute(), ocrDocumentType)
                .map(analysisId -> buildOcrDocumentSave(process, file, analysisId));

    }

    private OcrDocumentSaveVO buildOcrDocumentSave(Process process, File file, String analysisId) {
        return OcrDocumentSaveVO.builder()
                .name(file.getName())
                .storageId(file.getId())
                .storageRoute(file.getStorageRoute())
                .processId(process.getId())
                .analysisId(analysisId)
                .status(OcrStatus.PENDING)
                .build();
    }

}
