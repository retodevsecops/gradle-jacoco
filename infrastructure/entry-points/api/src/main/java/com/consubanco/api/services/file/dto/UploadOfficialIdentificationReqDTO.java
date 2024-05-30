package com.consubanco.api.services.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import reactor.core.publisher.Mono;

import static com.consubanco.api.commons.util.ParamsValidator.paramIsUrl;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UploadOfficialIdentificationReqDTO {

    @Schema(description = "Url of the image of the front part of the official ID", example = "https://official-id.com/front-official-id.jpg", requiredMode = REQUIRED)
    private String urlImageFrontOfficialID;

    @Schema(description = "Url of the image of the back part of the official ID", example = "https://official-id.com/back-official-id.jpg", requiredMode = REQUIRED)
    private String urlImageBackOfficialID;


    public Mono<UploadOfficialIdentificationReqDTO> check() {
        return Mono.zip(paramIsUrl(urlImageFrontOfficialID), paramIsUrl(urlImageBackOfficialID))
                .map(tuple -> this);
    }

    public String front() {
        return this.urlImageFrontOfficialID;
    }

    public String back() {
        return this.urlImageBackOfficialID;
    }

}
