package com.consubanco.api.services.agreement;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.services.agreement.constants.AgreementPathParams;
import com.consubanco.api.services.agreement.dto.AttachmentResDTO;
import com.consubanco.api.services.agreement.dto.GetAgreementResponseDTO;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

@UtilityClass
public class AgreementOpenAPI {

    private static final String TAG = "Agreement";

    public static Consumer<Builder> findByNumber() {
        return ops -> ops.tag(TAG)
                .operationId("agreementFindByNumber")
                .description("Find agreement by number.")
                .summary("Find agreement by number.")
                .parameter(ParamsOpenAPI.path(AgreementPathParams.AGREEMENT_NUMBER, "Agreement number"))
                .response(responseOk(GetAgreementResponseDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> getAttachments() {
        return ops -> ops.tag(TAG)
                .operationId("getAttachments")
                .description("List attachments required by an agreement associated with a process.")
                .summary("List attachments by agreement.")
                .parameter(ParamsOpenAPI.path(AgreementPathParams.PROCESS_ID, "Process identifier"))
                .response(responseOkWithList(AttachmentResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
