package com.consubanco.model.entities.file.constant;

import com.consubanco.model.entities.document.constant.DocumentNames;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileConstants {

    private static final String OFFER_DIRECTORY_PATH = "renewal/offer/%s/";
    private static final String PDF_FORMAT = "%s.pdf";
    private static final String ATTACHMENTS_DIRECTORY_PATH = OFFER_DIRECTORY_PATH.concat("attachments/");
    private static final String DOCUMENTS_DIRECTORY_PATH = OFFER_DIRECTORY_PATH.concat("documents/");

    private static final String CNCA_LETTERS_DIRECTORY_PATH = OFFER_DIRECTORY_PATH.concat("cnca/");
    public static final String MANAGEMENT_DIRECTORY_PATH = "management/vd-loans-documents-ms";

    public static String pdfFormat(String fileName) {
        return String.format(PDF_FORMAT, fileName);
    }

    public static String cncaDirectory(String offerId) {
        return String.format(CNCA_LETTERS_DIRECTORY_PATH, offerId);
    }

    public static String attachmentsDirectory(String offerId) {
        return String.format(ATTACHMENTS_DIRECTORY_PATH, offerId);
    }

    public static String documentsDirectory(String offerId) {
        return String.format(DOCUMENTS_DIRECTORY_PATH, offerId);
    }

    public static String offerDirectory(String offerId) {
        return String.format(OFFER_DIRECTORY_PATH, offerId);
    }

    public static String signedApplicantRecordRoute(String offerId) {
        String route = documentsDirectory(offerId).concat(DocumentNames.APPLICANT_RECORD);
        return pdfFormat(route);
    }

    public static String unsignedApplicantRecordRoute(String offerId) {
        String route = documentsDirectory(offerId).concat(DocumentNames.UNSIGNED_APPLICANT_RECORD);
        return pdfFormat(route);
    }

    public static String cncaLetterRoute(String offerId) {
        String route = cncaDirectory(offerId).concat(DocumentNames.CNCA_LETTER);
        return pdfFormat(route);
    }

    public static String attachmentRoute(String offerId, String attachmentName) {
        String directory = attachmentsDirectory(offerId);
        String baseRoute = directory.concat(attachmentName);
        return pdfFormat(baseRoute);
    }

}
