package com.consubanco.model.entities.loan;

import com.consubanco.model.entities.loan.constant.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    private Integer id;
    private String processId;
    private String otp;
    private String applicationStatus;
    private Map<String, Object> request;
    private Map<String, Object> response;
    private String offerStatus;
    private String emailStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean applicationIsSuccessful() {
        return ApplicationStatus.SUCCESSFUL.name().equalsIgnoreCase(this.applicationStatus);
    }


}
