package com.consubanco.model.entities.ocr.vo;

import lombok.Data;

@Data
public class OcrProofAddressVO {
    private String street;
    private String colony;
    private String zipCode;
    private String city;
    private String country;
    private String state;
    private String externalNumber;
    private String interiorNumber;
    private String delegation;
}