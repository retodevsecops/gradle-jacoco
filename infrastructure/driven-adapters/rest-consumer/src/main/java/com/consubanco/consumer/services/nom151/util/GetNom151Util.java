package com.consubanco.consumer.services.nom151.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GetNom151Util {

    private static final String REQUEST = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Body>\n" +
            "    <GETNOM xmlns=\"http://tempuri.org/\">\n" +
            "      <UsuarioServicio>%s</UsuarioServicio>\n" +
            "      <PasswordServicio>%s</PasswordServicio>\n" +
            "      <Identificador>%s</Identificador>\n" +
            "    </GETNOM>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";
    private static final String REGEX_SUCCESS_RESPONSE = "<GETNOMResult>(.*?)</GETNOMResult>";

    public static String buildRequest(String user, String password, String documentId) {
        validateCredentials(user, password);
        return String.format(REQUEST, user, password, documentId);
    }

    private void validateCredentials(String user, String password) {
        if (user == null || user.isEmpty()) throw new IllegalArgumentException("User cannot be null or empty");
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("Password cannot be null or empty");
    }

    public static String getSuccessfulResponse(String response) {
        return ApiResponseNom151Util.extractSoapResponse(response, REGEX_SUCCESS_RESPONSE);
    }

}
