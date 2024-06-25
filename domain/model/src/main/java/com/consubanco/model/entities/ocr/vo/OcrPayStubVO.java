package com.consubanco.model.entities.ocr.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OcrPayStubVO {
    private String firstName;
    private String secondName;
    private String paternalSurname;
    private String maternalSurname;
    private String employeeNumber;
    private String jobCode;
    private String rfc;
    private String payrollType;
    private LocalDate initialPayPeriod;
    private LocalDate finalPayPeriod;
    private String dependency;
}