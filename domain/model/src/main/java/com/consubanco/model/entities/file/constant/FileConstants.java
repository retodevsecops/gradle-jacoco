package com.consubanco.model.entities.file.constant;

import com.consubanco.model.entities.document.constant.DocumentNames;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileConstants {
    private final static String OFFER_DIRECTORY_PATH = "renewal/offer/%s/";
    public final static String FILE_NAME_CNCA_LETTER = "carta-de-liquidacion";
    private final static String PDF_FORMAT = "%s.pdf";
    private final static String ATTACHMENTS_DIRECTORY_PATH = OFFER_DIRECTORY_PATH.concat("attachments/");
    private final static String DOCUMENTS_DIRECTORY_PATH = OFFER_DIRECTORY_PATH.concat("documents/");

    private final static String CNCA_LETTERS_DIRECTORY_PATH = OFFER_DIRECTORY_PATH.concat("cnca/");
    public final static String MANAGEMENT_DIRECTORY_PATH = "management/puc-loans-documents-msa";

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

    public static String signedApplicantRecordDirectory(String offerId) {
        return documentsDirectory(offerId).concat(DocumentNames.APPLICANT_RECORD);
    }

    public static String unsignedApplicantRecordDirectory(String offerId) {
        return documentsDirectory(offerId).concat(DocumentNames.UNSIGNED_APPLICANT_RECORD);
    }

}
