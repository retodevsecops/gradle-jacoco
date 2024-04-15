package com.consubanco.model.entities.file.gateway;

import com.consubanco.model.entities.file.vo.AttachmentVO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface FileGateway {
    Mono<String> getContentCNCALetter(String loanId);
    Mono <String> generate(List<String> documents, List<AttachmentVO> attachments, Map<String, Object> payload);
    Mono <String> generate(String document, Map<String, Object> payload);
}
