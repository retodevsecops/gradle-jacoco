package com.consubanco.usecase.ocr.helpers;

import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrDocumentUpdateVO;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.consubanco.model.entities.ocr.constant.FailureReason.*;
import static com.consubanco.model.entities.ocr.constant.PayStubProperties.*;
import static com.consubanco.model.entities.ocr.message.OcrMessage.*;

@RequiredArgsConstructor
public class ValidateOcrDocumentsHelper {

    private final OcrDocumentGateway ocrDocumentGateway;
    private final OcrDocumentRepository ocrDocumentRepository;

    public Mono<List<OcrDocument>> execute(List<OcrDocument> ocrDocuments, Process process) {
        return ocrDocumentGateway.getDelayTime()
                .flatMap(Mono::delay)
                .flatMap(e -> analyzeOcrDocuments(ocrDocuments));
    }

    private Mono<List<OcrDocument>> analyzeOcrDocuments(List<OcrDocument> ocrDocuments) {
        return Flux.fromIterable(ocrDocuments)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::updateOcrDocument)
                .sequential()
                .collectList();
    }

    private Mono<OcrDocument> updateOcrDocument(OcrDocument ocrDocument) {
        return getOcrDocumentStatus(ocrDocument)
                .flatMap(ocrDocumentRepository::update);
    }

    private Mono<OcrDocumentUpdateVO> getOcrDocumentStatus(OcrDocument ocrDocument) {
        return ocrDocumentGateway.getAnalysisData(ocrDocument.getAnalysisId())
                .map(data -> validateOcrDocument(ocrDocument, data))
                .defaultIfEmpty(new OcrDocumentUpdateVO(ocrDocument.getId(), NOT_DATA_EXTRACTED))
                .onErrorResume(error -> Mono.just(new OcrDocumentUpdateVO(ocrDocument.getId(), FAILED_GET_METADATA, error.getMessage())));
    }

    private OcrDocumentUpdateVO validateOcrDocument(OcrDocument ocr, List<OcrDataVO> ocrData) {
        try {
            OcrDocumentType documentType = OcrDocumentType.getTypeFromName(ocr.getBaseName());
            return switch (documentType) {
                case PAY_STUBS -> checkPayStubs(ocr, ocrData);
                case PROOF_ADDRESS -> checkProofAddress(ocr, ocrData);
                case INE -> checkIne(ocr, ocrData);
            };
        } catch (TechnicalException exception) {
            return new OcrDocumentUpdateVO(ocr.getId(), ocrData, NOT_PROCESS);
        } catch (DateTimeParseException exception) {
            return new OcrDocumentUpdateVO(ocr.getId(), ocrData, INVALID_DATE_FORMAT, exception.getMessage());
        } catch (Exception exception) {
            return new OcrDocumentUpdateVO(ocr.getId(), ocrData, UNKNOWN_ERROR, exception.getMessage());
        }
    }

    private OcrDocumentUpdateVO checkPayStubs(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList) {
        Integer ocrId = ocrDocument.getId();
        Optional<OcrDataVO> fiscalFolio = getOcrDataByName(ocrDataList, FISCAL_FOLIO.getKey());
        Optional<OcrDataVO> initialPeriod = getOcrDataByName(ocrDataList, INITIAL_PERIOD_PAYMENT.getKey());
        Optional<OcrDataVO> finalPeriod = getOcrDataByName(ocrDataList, FINAL_PERIOD_PAYMENT.getKey());
        if (fiscalFolio.isEmpty()) return new OcrDocumentUpdateVO(ocrId, ocrDataList, FISCAL_FOLIO_NOT_FOUND);
        if (initialPeriod.isEmpty()) return new OcrDocumentUpdateVO(ocrId, ocrDataList, INITIAL_PAY_NOT_FOUND);
        if (finalPeriod.isEmpty()) return new OcrDocumentUpdateVO(ocrId, ocrDataList, FINAL_PAY_NOT_FOUND);
        LocalDate initialDate = stringToDate(initialPeriod.get().getValue());
        LocalDate finalDate = stringToDate(finalPeriod.get().getValue());
        long daysBetween = ChronoUnit.DAYS.between(initialDate, finalDate);
        int index = ocrDocument.getDocumentIndex();
        if (index == -1) return new OcrDocumentUpdateVO(ocrId, ocrDataList, NOT_INDEX);
        if (daysBetween >= 28 && daysBetween <= 31) {
            int minusMonths = index + 1;
            LocalDate expectedDate = LocalDate.now().minusMonths(minusMonths);
            if (isDateWithinPeriod(expectedDate, initialDate, finalDate)) {
                return new OcrDocumentUpdateVO(ocrId, ocrDataList);
            }
            String reason = invalidMonthlyPayStub(expectedDate, initialDate, finalDate);
            return new OcrDocumentUpdateVO(ocrId, ocrDataList, INVALID_DATE, reason);
        } else if (daysBetween >= 14 && daysBetween <= 16) {
            int weeks = index + 1;
            LocalDate[] fortnightDates = getFortnightDates(index);
            LocalDate expectedStartDate = LocalDate.now().minusWeeks((long) 2 * weeks);
            if (isDateWithinPeriod(expectedStartDate, initialDate, finalDate)) {
                return new OcrDocumentUpdateVO(ocrId, ocrDataList);
            }
            String reason = invalidFortnightPayStub(fortnightDates[0], fortnightDates[1], initialDate, finalDate);
            return new OcrDocumentUpdateVO(ocrId, ocrDataList, INVALID_DATE, reason);
        }
        String reason = unknownPeriodicity(initialDate, finalDate, daysBetween);
        return new OcrDocumentUpdateVO(ocrId, ocrDataList, UNKNOWN_PERIODICITY, reason);
    }

    public static LocalDate[] getFortnightDates(int index) {
        LocalDate currentDate = LocalDate.now();
        if (index == 0) {
            if(currentDate.getDayOfMonth() <= 15) {
                LocalDate startDate = currentDate.withDayOfMonth(1);
                LocalDate endDate = currentDate.withDayOfMonth(15);
                return new LocalDate[]{startDate, endDate};
            }
            LocalDate startDate = currentDate.withDayOfMonth(1);
            LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
            return new LocalDate[]{startDate, endDate};
        }
        if (index % 2 == 0) {
            LocalDate startDate = currentDate.minusMonths(index / 2).withDayOfMonth(1);
            LocalDate endDate = startDate.plusDays(14);
            return new LocalDate[]{startDate, endDate};
        }
        if(currentDate.getDayOfMonth() <= 15) {
            LocalDate startDate = currentDate.minusMonths((index + 1) / 2).withDayOfMonth(16);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            return new LocalDate[]{startDate, endDate};
        }
        LocalDate startDate = currentDate.minusMonths((index + 2) / 2).withDayOfMonth(16);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return new LocalDate[]{startDate, endDate};
    }


    private boolean isDateWithinPeriod(LocalDate expectedDate, LocalDate startDate, LocalDate endDate) {
        return (expectedDate.isEqual(startDate) || expectedDate.isAfter(startDate)) &&
                (expectedDate.isEqual(endDate) || expectedDate.isBefore(endDate));
    }

    private Optional<OcrDataVO> getOcrDataByName(List<OcrDataVO> ocrData, String name) {
        return ocrData.stream()
                .filter(data -> data.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public LocalDate stringToDate(String date) {
        String[] parts = date.split("/");
        String day = parts[0];
        String month = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase();
        String year = parts[2];
        String format = "%s/%s/%s";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);
        return LocalDate.parse(String.format(format, day, month, year), formatter);
    }

    private OcrDocumentUpdateVO checkProofAddress(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrData);
    }

    private OcrDocumentUpdateVO checkIne(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrData);
    }

}
