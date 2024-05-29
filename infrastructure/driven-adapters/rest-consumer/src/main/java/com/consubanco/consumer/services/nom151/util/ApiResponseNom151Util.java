package com.consubanco.consumer.services.nom151.util;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.document.message.DocumentMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.NOM151_UNEXPECTED_FORMAT;

public class ApiResponseNom151Util {

    private static final String ERROR_MESSAGE = "Api error response: %s - %s";
    private static final String REGEX_ERROR_CODE = "<ErrorCode>(.*?)</ErrorCode>";
    private static final String REGEX_ERROR_DESCRIPTION = "<ErrorDescription>(.*?)</ErrorDescription>";

    public static String extractSoapResponse(String response, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) return matcher.group(1).trim();
        throw ExceptionFactory.buildTechnical(DocumentMessage.formatNom151(regex), NOM151_UNEXPECTED_FORMAT);
    }

    public static String getErrorDetail(String response) {
        String errorCode = extractSoapResponse(response, REGEX_ERROR_CODE);
        String errorDescription = extractSoapResponse(response, REGEX_ERROR_DESCRIPTION);
        return String.format(ERROR_MESSAGE, errorCode, errorDescription);
    }

}
