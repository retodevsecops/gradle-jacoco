package com.consubanco.consumer.adapters.emailsender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EmailSenderRequest {
    private SendGenericMailRequestBO sendGenericMailRequestBO;

    @Data
    @Builder
    public static class SendGenericMailRequestBO {
        private String applicationId;
        private String customerBp;
        private String template;
        private Mail mail;
    }
    @Data
    @Builder
    public static class Mail {
        private List<String> to;
        private String subject;
        private String body;
        private String sign;
        private List<KeyValueDto> keyvalues;
        private List<AttachmentsDto> attachments;
    }
    @Data
    @Builder
    public static class KeyValueDto {
        private String key;
        private String value;
    }
    @Data
    @Builder
    public static class AttachmentsDto {
        private String fileName;
        private String fileB64;
        private boolean isProtected;
    }

}
