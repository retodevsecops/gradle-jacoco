package com.consubanco.model.entities.file.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AttachmentVO {
    private String name;
    private List<String> urls;
}
