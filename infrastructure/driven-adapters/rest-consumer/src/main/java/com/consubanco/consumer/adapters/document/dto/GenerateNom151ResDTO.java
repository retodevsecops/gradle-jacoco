package com.consubanco.consumer.adapters.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateNom151ResDTO {

    private ResponseData cdoDigitalSignatureResponseBO;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseData implements Serializable {
        private String status;
        private String code;
        private String response;
        private ResponseNom151 cdoInfo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseNom151 implements Serializable {
        private String id;
        private String hash;
        private String datetime;
        private String description;

        @JsonProperty("subsistema")
        private String subSystem;

        private String uid;
        private String ip;
        private String userName;

        @JsonProperty("apptokenid")
        private String appTokenId;

        @JsonProperty("apptokenDescription")
        private String appTokenDescription;
        private AppToken appToken;
        private String constanciaBase64;

        @JsonProperty("certificadoCDOBase64")
        private String certificateCdoBase64;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppToken implements Serializable {
        private String id;
        private String uid;

        @JsonProperty("clientid")
        private String clientId;

        private String datetime;
        private String description;
        private Boolean enable;
        private Boolean defaultFlg;
        private String idService;
        private String email;
    }

    public Boolean checkNom151() {
        return Objects.nonNull(this.cdoDigitalSignatureResponseBO) &&
                Objects.nonNull(this.cdoDigitalSignatureResponseBO.cdoInfo) &&
                Objects.nonNull(this.cdoDigitalSignatureResponseBO.cdoInfo.constanciaBase64);
    }

    public String getCertificateInBase64() {
        return this.cdoDigitalSignatureResponseBO.cdoInfo.constanciaBase64;
    }

}
