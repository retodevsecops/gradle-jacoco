package com.consubanco.api.services.loan.dto;

import com.consubanco.model.entities.loan.LoanApplication;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
public class LoanApplicationResDTO {

    @Schema(description = "Id of loan application.", example = "1", requiredMode = REQUIRED)
    private Integer id;

    @Schema(description = "Identifier process with the loan application.", example = "950dc9af-83d3-4fbe-bffd-09dfa7459d87", requiredMode = REQUIRED)
    private String processId;

    @Schema(description = "Otp code with which the application was made.", example = "542654", requiredMode = REQUIRED)
    private String otp;

    @Schema(description = "Application data in json format.", example = "{}", requiredMode = REQUIRED)
    private Map<String, Object> request;

    @Schema(description = "Application response data in json format.", example = "{}", requiredMode = REQUIRED)
    private Map<String, Object> response;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Created date.", example = "yyyy-MM-dd HH:mm:ss", requiredMode = REQUIRED)
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Update date.", example = "yyyy-MM-dd HH:mm:ss", requiredMode = REQUIRED)
    private LocalDateTime updatedAt;

    public LoanApplicationResDTO(LoanApplication loanApplication) {
        this.id = loanApplication.getId();
        this.otp = loanApplication.getOtp();
        this.processId = loanApplication.getProcessId();
        this.request = loanApplication.getRequest();
        this.response = loanApplication.getResponse();
        this.createdAt = loanApplication.getCreatedAt();
        this.updatedAt = loanApplication.getUpdatedAt();
    }
}
