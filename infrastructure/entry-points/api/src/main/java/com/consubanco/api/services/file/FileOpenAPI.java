package com.consubanco.api.services.file;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.commons.swagger.RequestsOpenAPI;
import com.consubanco.api.services.file.constants.FilePathParams;
import com.consubanco.api.services.file.dto.*;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

public class FileOpenAPI {

    private static final String TAG = "File";

    private static final String TAG_OFFER = "Offer Files";
    private static final String TAG_MANAGEMENT = "Management";

    public static Consumer<Builder> buildCNCALetters() {
        return ops -> ops.tag(TAG_OFFER)
                .operationId("buildCNCALetters")
                .description("Get CNCA letter by account number.")
                .summary("Get CNCA letter.")
                .requestBody(RequestsOpenAPI.body(BuildCNCALettersReqDTO.class))
                .response(responseOkWithList(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> generateFileWithDocuments() {
        return ops -> ops.tag(TAG)
                .operationId("generateDocument")
                .description("Generate a single PDF document as url from a list documents.")
                .summary("Generate pdf document as url.")
                .requestBody(RequestsOpenAPI.body(GenerateDocumentReqDTO.class))
                .response(responseOk(GenerateDocumentResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> generateFileEncoded() {
        return ops -> ops.tag(TAG)
                .operationId("generateFileEncoded")
                .description("Generate a single PDF document as file base64 encoded from a list documents.")
                .summary("Generate pdf document as base64 encoded.")
                .requestBody(RequestsOpenAPI.body(GenerateDocumentReqDTO.class))
                .response(responseOk(GenerateDocumentResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> getAndUpload() {
        return ops -> ops.tag(TAG)
                .operationId("getAndUpload")
                .description("Generate and save in google storage a single PDF document from a list documents.")
                .summary("Generate and save in storage pdf document.")
                .requestBody(RequestsOpenAPI.body(GetAndUploadDocumentReqDTO.class))
                .response(responseOk(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> uploadAgreementFiles() {
        return ops -> ops.tag(TAG_OFFER)
                .operationId("uploadAgreementFiles")
                .description("Upload all files of an agreement, including attachments and auto-generated documents.")
                .summary("Upload all files of an agreement.")
                .parameter(ParamsOpenAPI.path(FilePathParams.OFFER_ID, "Offer identifier"))
                .parameter(ParamsOpenAPI.path(FilePathParams.AGREEMENT_NUMBER, "Agreement number"))
                .response(responseAccepted(Void.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> getFilesByOffer() {
        return ops -> ops.tag(TAG_OFFER)
                .operationId("getFilesByOffer")
                .description("Get all offer documents including generated documents and attachments.")
                .summary("Get all files by offer.")
                .parameter(ParamsOpenAPI.path(FilePathParams.OFFER_ID, "Offer identifier"))
                .response(responseOkWithList(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> uploadPayloadTemplate() {
        return ops -> ops.tag(TAG_MANAGEMENT)
                .operationId("uploadPayloadTemplate")
                .description("Upload template file with which the payload is built to consume the api to generate documents.")
                .summary("Upload payload template file.")
                .response(responseOk(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
