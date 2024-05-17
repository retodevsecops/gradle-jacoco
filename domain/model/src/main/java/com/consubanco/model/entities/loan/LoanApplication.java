package com.consubanco.model.entities.loan;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {
    private Integer id;
    private String processId;
    private String otp;
    private Map<String, Object> request;
    private Map<String, Object> response;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LoanApplication(String processId, String otp, Map<String, Object> request, Map<String, Object> response) {
        this.processId = processId;
        this.otp = otp;
        this.request = request;
        this.response = response;
    }
}
