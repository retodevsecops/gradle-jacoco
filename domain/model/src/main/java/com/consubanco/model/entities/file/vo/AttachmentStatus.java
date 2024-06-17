package com.consubanco.model.entities.file.vo;


import com.consubanco.model.entities.file.constant.AttachmentStatusEnum;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentStatus {

    private AttachmentStatusEnum status;
    private List<InvalidAttachment> invalidAttachments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class InvalidAttachment {
        private String name;
        private String code;
        private String reason;
    }

}
