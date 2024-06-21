package com.consubanco.consumer.adapters.loan;

import com.consubanco.consumer.adapters.document.properties.ApisProperties;
import com.consubanco.consumer.adapters.loan.properties.LoanApisProperties;
import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.email.gateway.EmailGateway;
import com.consubanco.model.entities.loan.constant.ApplicationStatus;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import com.consubanco.model.entities.loan.vo.ApplicationResponseVO;
import com.consubanco.model.entities.process.Process;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_CUSTOMER_ERROR;
import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.CUSTOMER_ATTRIBUTE_NOT_FOUND;
import static com.consubanco.model.entities.loan.message.LoanTechnicalMessage.API_CREATE_APPLICATION_ERROR;

@Service
public class LoanAdapter implements LoanGateway {

    private final CustomLogger logger;
    private final WebClient apiConnectClient;
    private final LoanApisProperties apisProperties;
    private final ITemplateOperations templateOperations;
    private final WebClient renexClient;
    private final EmailGateway emailGateway;
    private final ApisProperties apis;

    public LoanAdapter(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                       final CustomLogger logger,
                       final LoanApisProperties apisProperties,
                       final ITemplateOperations templateOperations,
                       final EmailGateway emailGateway,
                       final @Qualifier("ApiRenexClient") WebClient renexClient,
                       final ApisProperties apisProp) {
        this.logger = logger;
        this.apiConnectClient = apiConnectClient;
        this.apisProperties = apisProperties;
        this.templateOperations = templateOperations;
        this.emailGateway = emailGateway;
        this.renexClient = renexClient;
        this.apis = apisProp;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> buildApplicationData(String createApplicationTemplate, Map<String, Object> data) {
        return templateOperations.process(createApplicationTemplate, data, Map.class)
                .map(map -> (Map<String, Object>) map);
    }

    @Override
    public Mono<ApplicationResponseVO> createApplication(Map<String, Object> applicationData) {
        logger.info("This is request body of create application", applicationData);
        return this.apiConnectClient.post()
                .uri(apisProperties.getApiCreateApplication())
                .bodyValue(applicationData)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(map -> new ApplicationResponseVO(getApplicationStatus(map), map))
                .onErrorMap(throwTechnicalError(API_CREATE_APPLICATION_ERROR));
    }

    @Override
    public Mono<String> sendMail(Process process, String signedRecordAsBase64) {
        return retrieveCustomerInfo(process)
                .flatMap(customerInfo -> emailGateway.sendEmail(getAttribute(customerInfo,"email"),
                                                                getAttribute(customerInfo,"bpId"),
                                                                getAttribute(customerInfo,"firstName"),
                                                                signedRecordAsBase64))
                .map(Object::toString);
    }

    private String getApplicationStatus(Map<String, Object> response) {
        Integer status = CreateApplicationResponseUtil.getCodeResponse(response);
        return  (status == HttpStatus.SC_OK) ? ApplicationStatus.SUCCESSFUL.name() : ApplicationStatus.ERROR.name();
    }
    private Mono<Map<String, Object>> retrieveCustomerInfo(Process process) {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiCustomerProcess(), process.getId())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_CUSTOMER_ERROR))
                .onErrorMap(throwTechnicalError(API_CUSTOMER_ERROR));
    }
    private String getAttribute(Map<String, Object> attributes, String attribute) {
        return Optional.ofNullable(attributes.getOrDefault(attribute, null))
                .map(Object::toString)
                .orElseThrow(() -> ExceptionFactory.buildBusiness(String.format("Customer attribute: %s not found in customer response:%s",
                                attribute,
                                attributes),
                        CUSTOMER_ATTRIBUTE_NOT_FOUND));
    }


}
