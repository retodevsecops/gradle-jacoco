package com.consubanco.model.entities.file.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AttachmentFileVO {
    private final String name;
    private final List<FileUploadVO> files;
}
