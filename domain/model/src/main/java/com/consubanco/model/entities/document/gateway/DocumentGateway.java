package com.consubanco.model.entities.document.gateway;

import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.document.vo.PreviousDocumentVO;
import com.consubanco.model.entities.file.vo.AttachmentVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface DocumentGateway {
    Mono<String> generateContentCNCALetter(String loanId);
    Integer validDaysCNCA();
    Mono<String> generate(List<String> documents, List<AttachmentVO> attachments, Map<String, Object> payload);
    Mono<String> generate(String document, Map<String, Object> payload);
    Mono<Map<String, String>> generateMultiple(List<String> documents, Map<String, Object> payload);
    Flux<PreviousDocumentVO>  getDocsFromPreviousApplication(String previousApplicationId, List<AttachmentConfigVO> docs);
}