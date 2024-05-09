package com.consubanco.api.services.file;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.commons.swagger.RequestsOpenAPI;
import com.consubanco.api.services.file.constants.FileParams;
import com.consubanco.api.services.file.dto.FileResDTO;
import com.consubanco.api.services.file.dto.GenerateDocumentReqDTO;
import com.consubanco.api.services.file.dto.GenerateDocumentResDTO;
import com.consubanco.api.services.file.dto.GetAndUploadDocumentReqDTO;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.Map;
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
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
                .response(responseOkWithList(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> generateFileWithDocuments() {
        return ops -> ops.tag(TAG)
                .operationId("generateDocument")
                .description("Generate a single PDF document as url from a list documents.")
                .summary("Generate pdf document as url.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
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
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
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
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
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
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
                .response(responseAccepted(Void.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }
    public static Consumer<Builder> getFilesByOffer() {
        return ops -> ops.tag(TAG_OFFER)
                .operationId("getFilesByOffer")
                .description("Get all offer documents including generated documents and attachments.")
                .summary("Get all files by offer.")
                .parameter(ParamsOpenAPI.path(FileParams.OFFER_ID, "Offer identifier"))
                .response(responseOkWithList(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }
    public static Consumer<Builder> getCustomerVisibleFiles() {
        return ops -> ops.tag(TAG_OFFER)
                .operationId("getCustomerVisibleFiles")
                .description("Get all files that the customer can view.")
                .summary("Get customer visible files.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
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

    public static Consumer<Builder> uploadAgreementsConfig() {
        return ops -> ops.tag(TAG_MANAGEMENT)
                .operationId("uploadAgreementsConfig")
                .description("Upload the configuration file of the agreements to storage.")
                .summary("Upload agreements configuration file.")
                .response(responseOk(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> getManagementFiles() {
        return ops -> ops.tag(TAG_MANAGEMENT)
                .operationId("getManagementFiles")
                .description("Get all the configuration files of the microservice.")
                .summary("Get all management files.")
                .response(responseOkWithList(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> getPayloadData() {
        return ops -> ops.tag(TAG_OFFER)
                .operationId("getPayloadData")
                .description("Get all the payload data with which the documents are constructed.")
                .summary("Get payload data.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
                .response(responseOkWithList(Map.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> uploadOfficialID() {
        return ops -> ops.tag(TAG_OFFER)
                .operationId("uploadOfficialID")
                .description("This operation is responsible for uploading an official identification file to storage..")
                .summary("Upload official identification.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
                .parameter(ParamsOpenAPI.query(FileParams.OFFICIAL_ID, "Url image official identification").required(true))
                .response(responseOk(FileResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
