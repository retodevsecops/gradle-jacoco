package com.consubanco.model.entities.ocr.vo;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OcrDataVO {
    private String name;
    private Double confidence;
    private String value;
}
