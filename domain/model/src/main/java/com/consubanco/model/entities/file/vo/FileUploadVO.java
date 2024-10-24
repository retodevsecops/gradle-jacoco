package com.consubanco.model.entities.file.vo;

import com.consubanco.model.entities.file.constant.FileExtensions;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVO {

    private String name;
    private String content;
    private String extension;
    private Double sizeInMB;

    public Boolean isNotPDF() {
        return !FileExtensions.PDF.equalsIgnoreCase(this.getExtension());
    }

}
