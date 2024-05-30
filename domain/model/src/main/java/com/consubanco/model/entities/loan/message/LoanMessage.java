package com.consubanco.model.entities.loan.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoanMessage {
    private final static String REQUIRED_FILES = "Offer %s has not generated the necessary files for the application.";

    public String requiredFiles(String offerId) {
        return String.format(REQUIRED_FILES, offerId);
    }

}
