package com.consubanco.usecase.ocr.helpers;

import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.util.FortnightDates;
import com.consubanco.model.entities.ocr.OcrAnalysisResult;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrFailureReason;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.message.OcrTechnicalMessage;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrUpdateVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ValidateOcrDocumentsHelperTest {

    @Mock
    private OcrDocumentGateway ocrGateway;

    @Mock
    private OcrDocumentRepository ocrRepository;

    @InjectMocks
    private ValidateOcrDocumentsHelper validateOcrDocumentsHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(ocrGateway.getDelayTime()).thenReturn(Mono.just(Duration.ofMillis(1)));
        when(ocrGateway.getDaysRangeForPayStubsValidation()).thenReturn(45);
        when(ocrGateway.getConfidence()).thenReturn(0.9);
        when(ocrRepository.update(any(OcrUpdateVO.class))).thenAnswer(this::ocrUpdateToOcrDocument);
    }

    @Test
    void shouldDocumentsWithSuccessStatusWhenPayStubsAreValid() {
        List<OcrDocument> ocrDocuments = payStubs();
        when(ocrGateway.getAnalysisData(ocrDocuments.get(0).getAnalysisId())).thenReturn(validDataLastPayStub());
        when(ocrGateway.getAnalysisData(ocrDocuments.get(1).getAnalysisId())).thenReturn(validDataPenultimatePayStub());
        StepVerifier.create(validateOcrDocumentsHelper.execute(ocrDocuments))
                .expectNextMatches(docs -> docs.stream()
                        .allMatch(doc -> doc.getAnalysisResult().getStatus().equals(OcrStatus.SUCCESS)))
                .verifyComplete();
    }

    @Test
    void shouldDocumentsWithFailStatusWhenDateOfPayStubsIsInvalid() {
        List<OcrDocument> ocrDocuments = List.of(lastPayStub());
        when(ocrGateway.getAnalysisData(ocrDocuments.get(0).getAnalysisId())).thenReturn(invalidDateDataPayStub());
        StepVerifier.create(validateOcrDocumentsHelper.execute(ocrDocuments))
                .expectNextMatches(docs -> docs.stream()
                        .allMatch(doc -> {
                            var checkStatus = doc.getAnalysisResult().getStatus().equals(OcrStatus.FAILED);
                            var checkCode = doc.getAnalysisResult().getFailureCode().equals(OcrFailureReason.INVALID_DATE.name());
                            return checkStatus && checkCode;
                        }))
                .verifyComplete();
    }

    @Test
    void shouldDocumentsWithFailStatusWhenDuplicatePayStubs() {
        List<OcrDocument> ocrDocuments = payStubs();
        when(ocrGateway.getAnalysisData(ocrDocuments.get(0).getAnalysisId())).thenReturn(validDataLastPayStub());
        when(ocrGateway.getAnalysisData(ocrDocuments.get(1).getAnalysisId())).thenReturn(validDataLastPayStub());
        StepVerifier.create(validateOcrDocumentsHelper.execute(ocrDocuments))
                .expectNextMatches(docs -> docs.stream()
                        .allMatch(doc -> {
                            var checkStatus = doc.getAnalysisResult().getStatus().equals(OcrStatus.FAILED);
                            var checkCode = doc.getAnalysisResult().getFailureCode().equals(OcrFailureReason.DUPLICATE_PAY_STUB.name());
                            return checkStatus && checkCode;
                        }))
                .verifyComplete();
    }

    @Test
    void shouldDocumentsWithSuccessStatusWhenValidProofAddress() {
        List<OcrDocument> ocrDocuments = ocrDocumentsWithPayStubsAndProofAddress();
        when(ocrGateway.getAnalysisData(ocrDocuments.get(0).getAnalysisId())).thenReturn(validDataLastPayStub());
        when(ocrGateway.getAnalysisData(ocrDocuments.get(1).getAnalysisId())).thenReturn(validDataPenultimatePayStub());
        when(ocrGateway.getAnalysisData(ocrDocuments.get(2).getAnalysisId())).thenReturn(validDataProofAddress());
        StepVerifier.create(validateOcrDocumentsHelper.execute(ocrDocuments))
                .expectNextMatches(docs -> docs.stream()
                        .allMatch(doc -> doc.getAnalysisResult().getStatus().equals(OcrStatus.SUCCESS)))
                .verifyComplete();
    }

    @Test
    void shouldDocumentsWithSuccessStatusWhenValidIne() {
        List<OcrDocument> ocrDocuments = ocrDocumentsWithPayStubsProofAddressIne();
        when(ocrGateway.getAnalysisData(ocrDocuments.get(0).getAnalysisId())).thenReturn(validDataLastPayStub());
        when(ocrGateway.getAnalysisData(ocrDocuments.get(1).getAnalysisId())).thenReturn(validDataPenultimatePayStub());
        when(ocrGateway.getAnalysisData(ocrDocuments.get(2).getAnalysisId())).thenReturn(validDataProofAddress());
        when(ocrGateway.getAnalysisData(ocrDocuments.get(3).getAnalysisId())).thenReturn(anyData());
        StepVerifier.create(validateOcrDocumentsHelper.execute(ocrDocuments))
                .expectNextMatches(docs -> docs.stream()
                        .allMatch(doc -> doc.getAnalysisResult().getStatus().equals(OcrStatus.SUCCESS)))
                .verifyComplete();
    }

    @Test
    void shouldDocumentsWithFailStatusWhenFailedGetMetadata() {
        List<OcrDocument> ocrDocuments = List.of(lastPayStub());
        TechnicalException exception = new TechnicalException(OcrTechnicalMessage.API_GET_METADATA_ERROR);
        when(ocrGateway.getAnalysisData(ocrDocuments.get(0).getAnalysisId())).thenReturn(Mono.error(exception));
        StepVerifier.create(validateOcrDocumentsHelper.execute(ocrDocuments))
                .expectNextMatches(docs -> docs.stream()
                        .allMatch(doc -> doc.getAnalysisResult().getStatus().equals(OcrStatus.FAILED)))
                .verifyComplete();
    }

    private Mono<OcrDocument> ocrUpdateToOcrDocument(InvocationOnMock invocation) {
        OcrUpdateVO ocrUpdateVO = invocation.getArgument(0);
        return Mono.just(OcrDocument.builder()
                .id(ocrUpdateVO.getId())
                .data(ocrUpdateVO.getData())
                .analysisResult(OcrAnalysisResult.builder()
                        .status(ocrUpdateVO.getStatus())
                        .failureCode(ocrUpdateVO.getFailureCode())
                        .failureReason(ocrUpdateVO.getFailureReason())
                        .build())
                .build());
    }

    private List<OcrDocument> ocrDocumentsWithPayStubsAndProofAddress() {
        return List.of(lastPayStub(), penultimatePayStub(), proofAddress());
    }

    private List<OcrDocument> ocrDocumentsWithPayStubsProofAddressIne() {
        return List.of(lastPayStub(), penultimatePayStub(), proofAddress(), ine());
    }

    private List<OcrDocument> payStubs() {
        return List.of(lastPayStub(), penultimatePayStub());
    }

    private OcrDocument lastPayStub() {
        return OcrDocument.builder()
                .id(1)
                .name("recibo-nomina-0")
                .analysisId("126153499026994755042622584822378901172")
                .storageId("offer/241011767/attachments/recibo-nomina-0.pdf/1728441580015346")
                .build();
    }

    private OcrDocument penultimatePayStub() {
        return OcrDocument.builder()
                .id(2)
                .name("recibo-nomina-1")
                .analysisId("106289300244977654977298422950953173686")
                .storageId("offer/241011767/attachments/recibo-nomina-0.pdf/5")
                .build();
    }

    private Mono<List<OcrDataVO>> validDataLastPayStub() {
        LocalDate[] fortnightDates = FortnightDates.getDatesFromIndex(0, 15);
        return Mono.just(List.of(
                OcrDataVO.builder()
                        .name("numeroEmpleado")
                        .value("6004898")
                        .confidence(0.9992814636230469)
                        .build(),
                OcrDataVO.builder()
                        .name("folio-fiscal")
                        .value("720DE2D0-4403-494B-8400-B25E15A9FF82")
                        .confidence(0.95)
                        .build(),
                OcrDataVO.builder()
                        .name("periodo-inicial-pago")
                        .value(formatDate(fortnightDates[0]))
                        .confidence(0.9978657531738281)
                        .build(),
                OcrDataVO.builder()
                        .name("periodo-final-pago")
                        .value(formatDate(fortnightDates[1]))
                        .confidence(0.9973002624511719)
                        .build()));
    }

    private Mono<List<OcrDataVO>> validDataPenultimatePayStub() {
        LocalDate[] fortnightDates = FortnightDates.getDatesFromIndex(1, 15);
        return Mono.just(List.of(
                OcrDataVO.builder()
                        .name("numeroEmpleado")
                        .value("6004898")
                        .confidence(0.9992814636230469)
                        .build(),
                OcrDataVO.builder()
                        .name("folio-fiscal")
                        .value("44A642F5-517F-4039-9F8E-040D3497C33C")
                        .confidence(0.95)
                        .build(),
                OcrDataVO.builder()
                        .name("periodo-inicial-pago")
                        .value(formatDate(fortnightDates[0]))
                        .confidence(0.9978657531738281)
                        .build(),
                OcrDataVO.builder()
                        .name("periodo-final-pago")
                        .value(formatDate(fortnightDates[1]))
                        .confidence(0.9973002624511719)
                        .build()));
    }

    private String formatDate(LocalDate date) {
        String format = "%02d/%02d/%d";
        return String.format(format, date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    private Mono<List<OcrDataVO>> invalidDateDataPayStub() {
        return Mono.just(List.of(
                OcrDataVO.builder()
                        .name("folio-fiscal")
                        .value("720DE2D0-4403-494B-8400-B25E15A9FF82")
                        .confidence(0.95)
                        .build(),
                OcrDataVO.builder()
                        .name("periodo-inicial-pago")
                        .value("16/04/2023")
                        .confidence(0.9978657531738281)
                        .build(),
                OcrDataVO.builder()
                        .name("periodo-final-pago")
                        .value("30/04/2023")
                        .confidence(0.9973002624511719)
                        .build()));
    }

    private OcrDocument proofAddress() {
        return OcrDocument.builder()
                .id(1)
                .name("comprobante-domicilio")
                .analysisId("206289300244977654977298422950953173682")
                .storageId("offer/241011767/attachments/comprobante-domicilio.pdf/5")
                .build();
    }

    private OcrDocument ine() {
        return OcrDocument.builder()
                .id(1)
                .name("identificacion-oficial")
                .analysisId("706289300244977654977298422950953173687")
                .storageId("offer/241011767/attachments/identificacion-oficial.pdf/5")
                .build();
    }

    private Mono<List<OcrDataVO>> validDataProofAddress() {
        LocalDate[] fortnightDates = FortnightDates.getDatesFromIndex(1, 15);
        return Mono.just(List.of(
                OcrDataVO.builder()
                        .name("codigo-postal")
                        .value("6004898")
                        .confidence(0.99)
                        .build(),
                OcrDataVO.builder()
                        .name("vigencia")
                        .value(formatDate(fortnightDates[0]))
                        .confidence(0.95)
                        .build()));
    }

    private Mono<List<OcrDataVO>> anyData() {
        return Mono.just(List.of(
                OcrDataVO.builder()
                        .name("any-data")
                        .value("6004898")
                        .confidence(0.99)
                        .build()));
    }

}