package com.consubanco.consumer.services.nom151.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
@AllArgsConstructor
public class LoadDocumentReqDTO {

    private static final String RESULT_IS_SUCCESS = "1";
    private static final String REQUEST = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Body>\n" +
            "    <Load xmlns=\"http://tempuri.org/\">\n" +
            "      <UsuarioServicio>%s</UsuarioServicio>\n" +
            "      <PasswordServicio>%s</PasswordServicio>\n" +
            "      <Identificador>%s</Identificador>\n" +
            "      <Nombres>%s</Nombres>\n" +
            "      <ApPaterno>%s</ApPaterno>\n" +
            "      <ApMaterno>%s</ApMaterno>\n" +
            "      <RFC>%s</RFC>\n" +
            "      <Email>%s</Email>\n" +
            "      <DocumentoBase64>%s</DocumentoBase64>\n" +
            "      <MostrarFirmas>%s</MostrarFirmas>\n" +
            "    </Load>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";
    private static final String REGEX_SUCCESS_RESPONSE = "<LoadResult>(.*?)</LoadResult>";
    private String documentId;
    private String names;
    private String paternalLastname;
    private String rfc;
    private String email;
    private String documentInBase64;

    @Builder.Default
    private String motherLastname = "";

    @Builder.Default
    private boolean showSignatures = true;

    public String buildRequest(String user, String password) {
        validateCredentials(user, password);
        validateFields();
        return String.format(REQUEST, user, password, documentId, names, paternalLastname, motherLastname, rfc,
                email, documentInBase64, showSignatures);
    }

    public static String getSuccessfulResponse(String response) {
        return ApiResponseNom151Util.extractSoapResponse(response, REGEX_SUCCESS_RESPONSE);
    }

    public static boolean resultIsSuccess(String result) {
        return result.equals(RESULT_IS_SUCCESS);
    }

    private void validateCredentials(String user, String password) {
        if (user == null || user.isEmpty()) throw new IllegalArgumentException("User cannot be null or empty");
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password cannot be null or empty");
    }

    private void validateFields() {
        if (documentId == null || documentId.isEmpty())
            throw new IllegalArgumentException("Document ID cannot be null or empty");
        if (names == null || names.isEmpty()) throw new IllegalArgumentException("Names cannot be null or empty");
        if (paternalLastname == null || paternalLastname.isEmpty())
            throw new IllegalArgumentException("Paternal Lastname cannot be null or empty");
        if (rfc == null || rfc.isEmpty()) throw new IllegalArgumentException("RFC cannot be null or empty");
        if (email == null || email.isEmpty()) throw new IllegalArgumentException("Email cannot be null or empty");
        if (documentInBase64 == null || documentInBase64.isEmpty())
            throw new IllegalArgumentException("Document Base64 cannot be null or empty");
    }

}
