package com.consubanco.consumer.adapters.emailsender;

import com.consubanco.consumer.adapters.document.properties.ApisProperties;
import com.consubanco.consumer.adapters.emailsender.dto.EmailSenderRequest;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.email.gateway.EmailGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.consubanco.consumer.commons.ClientExceptionFactory.requestError;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.loan.message.LoanTechnicalMessage.API_CREATE_APPLICATION_ERROR;
import static com.consubanco.model.entities.loan.message.LoanTechnicalMessage.REQUEST_API_ERROR;

@Service
public class EmailSenderAdapter implements EmailGateway {

    private final WebClient sendGenericEmailClient;
    private final ApisProperties apis;
    private final CustomLogger logger;
    private static final String templateId = "csb-renex-confirmacion-credito-mail";

    public EmailSenderAdapter(final @Qualifier("ApiConnectClient") WebClient sendGenericEmailClient,
                           final ApisProperties apisProperties,
                           final CustomLogger logger) {
        this.sendGenericEmailClient = sendGenericEmailClient;
        this.apis = apisProperties;
        this.logger = logger;
    }

    @Override
    public Mono<Boolean> sendEmail(String email, String bp, String fullName, String base64File) {
        logger.info("Sending email contract to customer BP: " + bp + "email: " + email + "contract : " + base64File.substring(0, 200));
        var bodyRequest = buildBodyRequest(email, bp, fullName, base64File);
        logger.info("body request sendGenericEmail: " + bodyRequest.toString());
        return this.sendGenericEmailClient.post()
                .uri(apis.getApiConnect().getApiSendGenericEmail())
                .bodyValue(bodyRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> Objects.nonNull(response) ? Boolean.TRUE : Boolean.FALSE)
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, REQUEST_API_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_CREATE_APPLICATION_ERROR));
    }

    private EmailSenderRequest buildBodyRequest(String email, String bp, String fullName, String base64File) {
        var mail = EmailSenderRequest.Mail.builder()
                .to(List.of(email))
                .subject("Confirmación Renovación Crédito")
                .keyvalues(List.of(EmailSenderRequest.KeyValueDto.builder()
                                .key("CUSTOMER_NAME")
                                .value(fullName)
                        .build()))
                .attachments(List.of(EmailSenderRequest.AttachmentsDto.builder()
                                .fileName("Formulario")
                                .fileB64(base64File)
                                .isProtected(false)
                        .build()))
                .body(" ")
                .sign(" ")
                .build();
        var request = EmailSenderRequest.SendGenericMailRequestBO.builder()
                .applicationId(apis.getApiConnect().getApplicationId())
                .customerBp(bp)
                .template(templateId)
                .mail(mail)
                .build();

        return EmailSenderRequest.builder()
                .sendGenericMailRequestBO(request)
                .build();
    }
}
