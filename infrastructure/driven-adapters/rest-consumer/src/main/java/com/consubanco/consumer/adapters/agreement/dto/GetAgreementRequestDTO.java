package com.consubanco.consumer.adapters.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetAgreementRequestDTO {

    @JsonProperty("numero")
    private String agreementNumber;

    @JsonProperty("canal")
    private String channel;

    private Boolean isCNCA;
    private Boolean isLCOM;

    public GetAgreementRequestDTO(String agreementNumber, String channel) {
        this.agreementNumber = agreementNumber;
        this.channel = channel;
    }

}
