package com.consubanco.model.entities.file.vo;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.message.FileBusinessMessage;
import com.consubanco.model.entities.file.message.FileMessage;
import lombok.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVO {

    private String name;
    private String content;
    private String extension;
    private Double sizeInMB;

    public Boolean isPDF() {
        return FileExtensions.PDF.equalsIgnoreCase(this.getExtension());
    }

    public Boolean isNotPDF() {
        return !FileExtensions.PDF.equalsIgnoreCase(this.getExtension());
    }

    public Mono<FileUploadVO> check() {
        if (Objects.isNull(content) || content.isBlank() || Objects.isNull(extension) || extension.isBlank()) {
            return ExceptionFactory.monoBusiness(FileBusinessMessage.DATA_MISSING_TO_UPLOAD, FileMessage.DATA_MISSING);
        }
        return Mono.just(this);
    }

}
