package com.consubanco.api.services.file;

import com.consubanco.api.commons.util.BytesUtil;
import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.file.constants.FilePathParams;
import com.consubanco.api.services.file.dto.*;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.usecase.file.BuildCNCALettersUseCase;
import com.consubanco.usecase.file.FileUseCase;
import com.consubanco.usecase.file.GenerateDocumentUseCase;
import com.consubanco.usecase.file.UploadAgreementFilesUseCase;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private final FileUseCase fileUseCase;
    private final BuildCNCALettersUseCase buildCNCALettersUseCase;
    private final GenerateDocumentUseCase generateDocUseCase;
    private final UploadAgreementFilesUseCase uploadFilesAgreementUseCase;

    public Mono<ServerResponse> buildCNCALetters(ServerRequest request) {
        return request.bodyToMono(BuildCNCALettersReqDTO.class)
                .map(BuildCNCALettersReqDTO::getOffer)
                .flatMapMany(offerDTO -> buildCNCALettersUseCase.execute(offerDTO.getId(), offerDTO.getLoansId()))
                .map(FileResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> generateFileWithDocuments(ServerRequest request) {
        return request.bodyToMono(GenerateDocumentReqDTO.class)
                .map(GenerateDocumentReqDTO::buildFileDataVO)
                .flatMap(generateDocUseCase::getAsUrl)
                .map(GenerateDocumentResDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> generateFileEncoded(ServerRequest request) {
        return request.bodyToMono(GenerateDocumentReqDTO.class)
                .map(GenerateDocumentReqDTO::buildFileDataVO)
                .flatMap(generateDocUseCase::getAsEncodedFile)
                .map(GenerateDocumentResDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> getAndUpload(ServerRequest request) {
        return request.bodyToMono(GetAndUploadDocumentReqDTO.class)
                .flatMap(req -> generateDocUseCase.getAndUpload(req.getData(), req.getOfferId(), req.getFileName()))
                .map(FileResDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> uploadAgreementFiles(ServerRequest request) {
        String agreement = request.pathVariable(FilePathParams.AGREEMENT_NUMBER);
        String offer = request.pathVariable(FilePathParams.OFFER_ID);
        return request.body(BodyExtractors.toParts())
                .cast(FilePart.class)
                .flatMap(this::buildFileUploadVO)
                .collectList()
                .flatMap(files -> uploadFilesAgreementUseCase.execute(agreement, offer, files))
                .flatMap(HttpResponseUtil::accepted);
    }

    public Mono<ServerResponse> getFilesByOffer(ServerRequest request) {
        String offerId = request.pathVariable(FilePathParams.OFFER_ID);
        return fileUseCase.getFilesByOffer(offerId)
                .map(FileResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

    private Mono<FileUploadVO> buildFileUploadVO(FilePart file) {
        return DataBufferUtils.join(file.content())
                .map(BytesUtil::getBytesFromBuffer)
                .map(fileContentBytes -> FileUploadVO.builder()
                        .name(file.name())
                        .extension(StringUtils.getFilenameExtension(file.filename()))
                        .content(Base64.encodeBase64String(fileContentBytes))
                        .build());
    }

}
