package com.consubanco.usecase.file.helpers;

import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.consubanco.model.entities.file.vo.AttachmentFileVO;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class PdfConvertHelper {

    private final PDFDocumentGateway pdfDocumentGateway;

    public Mono<String> convertAttachmentToPDF(FileUploadVO fileUploadVO) {
        return Mono.just(fileUploadVO)
                .filter(FileUploadVO::isNotPDF)
                .map(FileUploadVO::getContent)
                .map(List::of)
                .flatMap(pdfDocumentGateway::generatePdfWithImages)
                .defaultIfEmpty(fileUploadVO.getContent());
    }

    public Mono<String> buildMergedFile(AttachmentFileVO attachmentFileVO) {
        return Flux.fromIterable(attachmentFileVO.getFiles())
                .flatMap(this::convertAttachmentToPDF)
                .collectList()
                .flatMap(pdfDocumentGateway::merge);
    }

    public Mono<String> merge(List<String> base64Documents) {
        return pdfDocumentGateway.merge(base64Documents);
    }

}