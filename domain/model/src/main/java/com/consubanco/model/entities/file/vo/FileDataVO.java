package com.consubanco.model.entities.file.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDataVO {
    private List<String> documents;
    private List<AttachmentVO> attachments;
}
