package com.consubanco.model.entities.rpa;

import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CartaLibranza {

    private String applicationId;
    private String channel;
    private Boolean letterIsValidate;
    private String motive;
    private String promoterBp;
    private String customerBp;
    private String offerId;
    private Enterprise enterprise;
    private Letter letter;
    private List<FileUploadVO> files;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Letter {
        private String folio;
        private LocalDate date;
        private LocalDate validity;
        private Double discountAmount;
        private Double totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Enterprise {
        private String acronym;
        private String name;
    }

}
