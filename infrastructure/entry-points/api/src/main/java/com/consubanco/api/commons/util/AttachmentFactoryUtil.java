package com.consubanco.api.commons.util;

import com.consubanco.model.entities.file.vo.AttachmentFileVO;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.experimental.UtilityClass;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class AttachmentFactoryUtil {

    private static final String FILE_NAME_STRUCTURE = "%s-%s";

    public Mono<List<AttachmentFileVO>> extractAttachments(final ServerRequest request) {
        Map<String, List<FileUploadVO>> attachmentFilesMap = new HashMap<>();
        Map<String, AtomicInteger> fileCounters = new HashMap<>();
        return request.body(BodyExtractors.toParts())
                .ofType(FilePart.class)
                .flatMap(filePart -> addFileToAttachments(attachmentFilesMap, fileCounters, filePart))
                .then(Mono.just(attachmentFilesMap))
                .map(AttachmentFactoryUtil::buildAttachmentList);

    }

    private Mono<Void> addFileToAttachments(Map<String, List<FileUploadVO>> attachmentFilesMap,
                                            Map<String, AtomicInteger> fileCounters,
                                            FilePart filePart) {
        String fileName = defineFileName(filePart, fileCounters);
        return FilePartUtil.buildFileUploadVOFromFilePart(filePart, fileName)
                .map(fileUploadVO -> attachmentFilesMap.computeIfAbsent(filePart.name(), key -> new ArrayList<>()).add(fileUploadVO))
                .then();
    }

    private String defineFileName(FilePart filePart, Map<String, AtomicInteger> counters) {
        String attachmentName = filePart.name();
        int pos = counters.computeIfAbsent(attachmentName, key -> new AtomicInteger(1)).getAndIncrement();
        return String.format(FILE_NAME_STRUCTURE, attachmentName, pos);
    }

    private List<AttachmentFileVO> buildAttachmentList(Map<String, List<FileUploadVO>> attachmentFilesMap) {
        List<AttachmentFileVO> attachmentFileVOs = new ArrayList<>();
        attachmentFilesMap.forEach((name, files) -> attachmentFileVOs.add(new AttachmentFileVO(name, files)));
        return attachmentFileVOs;
    }

}
