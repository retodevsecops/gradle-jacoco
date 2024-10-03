package com.consubanco.api.services.rpa.dto;

import com.consubanco.model.entities.file.util.FileUtil;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.model.entities.rpa.CartaLibranza;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UploadCartaLibranzaReqDTO {

    @JsonProperty("sendCartaLibranzaReqBO")
    private DataDTO data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "Request DTO for Send Carta Libranza operation")
    public static class DataDTO {

        @Schema(description = "Unique ID for the application.", example = "CSB-RENEX", requiredMode = REQUIRED)
        private String applicationId;

        @Schema(description = "Channel through which the application was made.", example = "RENEX", requiredMode = REQUIRED)
        private String channel;

        @Schema(description = "List of files associated with the application.", requiredMode = REQUIRED)
        private List<FileDTO> files;

        @Schema(description = "Indicates if the letter is validated.", example = "true", requiredMode = REQUIRED)
        private Boolean letterIsValidate;

        @Schema(description = "Motive for the request.", example = "Motivo de la solicitud", requiredMode = REQUIRED)
        private String motive;

        @Schema(description = "Business partner ID for the promoter.", example = "004252386", requiredMode = REQUIRED)
        @JsonProperty("promoterBP")
        private String promoterBp;

        @Schema(description = "Business partner ID for the customer.", example = "007546754", requiredMode = REQUIRED)
        @JsonProperty("customerBP")
        private String customerBp;

        @Schema(description = "Reference folio for the operation.", example = "854765476543", requiredMode = REQUIRED)
        private String folio;

        @Schema(description = "Details of the enterprise associated with the request.", requiredMode = REQUIRED)
        private Enterprise enterprise;

        @Schema(description = "Details of the letter associated with the request.", requiredMode = REQUIRED)
        private Letter letter;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "DTO for file details including name and base64 content.")
    public static class FileDTO {

        @Schema(description = "Name of the file.", requiredMode = REQUIRED, example = "carta-libranza")
        @JsonProperty("fileName")
        private String name;

        @Schema(description = "Base64 encoded content of the file.", requiredMode = REQUIRED, example = "RXN0byBlcyB1biBlamVtcGxvIGRlIHVuYSBjb2RpZmljYWNpb24gZW4gYmFzZTY0")
        @JsonProperty("base64")
        private String base64Content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "Details of the letter.")
    public static class Letter {

        @Schema(description = "Folio of the letter.", example = "folio", requiredMode = REQUIRED)
        private String folio;

        @Schema(description = "Date of the letter.", example = "2023-09-01", requiredMode = REQUIRED)
        private LocalDate date;

        @Schema(description = "Validity date of the letter.", example = "2023-09-30", requiredMode = REQUIRED)
        @JsonProperty("validity")
        private LocalDate validity;

        @Schema(description = "Discount amount applied to the letter.", example = "150.00", requiredMode = REQUIRED)
        private Double discountAmount;

        @Schema(description = "Total amount of the letter.", example = "500.00", requiredMode = REQUIRED)
        private Double totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "Details of the enterprise associated with the request.")
    public static class Enterprise {

        @Schema(description = "Abbreviation of the enterprise.", example = "CSB", requiredMode = REQUIRED)
        @JsonProperty("sigla")
        private String acronym;

        @Schema(description = "Name of the enterprise.", example = "Consubanco", requiredMode = REQUIRED)
        @JsonProperty("name")
        private String name;
    }

    public CartaLibranza toModel() {
        return CartaLibranza.builder()
                .applicationId(this.data.applicationId)
                .channel(this.data.channel)
                .letterIsValidate(this.data.letterIsValidate)
                .motive(this.data.motive)
                .promoterBp(this.data.promoterBp)
                .customerBp(this.data.customerBp)
                .offerId(this.data.folio)
                .enterprise(CartaLibranza.Enterprise.builder()
                        .acronym(this.data.enterprise.acronym)
                        .name(this.data.enterprise.name)
                        .build())
                .letter(CartaLibranza.Letter.builder()
                        .folio(this.data.letter.folio)
                        .date(this.data.letter.date)
                        .discountAmount(this.data.letter.discountAmount)
                        .totalAmount(this.data.letter.totalAmount)
                        .validity(this.data.letter.validity)
                        .build())
                .files(this.data.files.stream()
                        .map(fileDTO -> FileUploadVO.builder()
                                .name(FileUtil.nameWithoutExtension(fileDTO.getName()))
                                .content(fileDTO.getBase64Content())
                                .extension(FileUtil.extensionFromFileName(fileDTO.getName()))
                                .sizeInMB(FileUtil.sizeInMBFromBase64(fileDTO.getBase64Content()))
                                .build())
                        .toList())
                .build();
    }

}
