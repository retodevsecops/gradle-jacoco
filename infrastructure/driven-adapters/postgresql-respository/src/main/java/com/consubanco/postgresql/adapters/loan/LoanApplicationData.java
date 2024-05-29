package com.consubanco.postgresql.adapters.loan;

import com.consubanco.model.entities.loan.LoanApplication;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_application")
public class LoanApplicationData {

    @Id
    private Integer id;

    @Column("process_id")
    private String processId;
    private String otp;
    private Json request;
    private Json response;

    @Column("application_status")
    private String applicationStatus;

    @Column("offer_status")
    private String offerStatus;

    @Column("email_status")
    private String emailStatus;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    public LoanApplication toEntityModel(LoanApplication loanApplication) {
        return loanApplication.toBuilder()
                .id(this.id)
                .processId(this.processId)
                .otp(this.otp)
                .applicationStatus(this.applicationStatus)
                .offerStatus(this.offerStatus)
                .emailStatus(this.emailStatus)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public LoanApplicationData updateOfferAndEmailStatus(String offerStatus, String emailStatus) {
        this.offerStatus = offerStatus;
        this.emailStatus = emailStatus;
        return this;
    }

}
