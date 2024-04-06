package com.consubanco.model.entities.file.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVO {
    private String name;
    private String content;
    private String extension;
}
