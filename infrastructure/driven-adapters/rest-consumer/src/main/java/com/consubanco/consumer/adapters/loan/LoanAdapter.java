package com.consubanco.consumer.adapters.loan;

import com.consubanco.consumer.adapters.loan.properties.LoanApisProperties;
import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.loan.constant.ApplicationStatus;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import com.consubanco.model.entities.loan.vo.ApplicationResponseVO;
import com.consubanco.model.entities.process.Process;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.loan.message.LoanTechnicalMessage.API_CREATE_APPLICATION_ERROR;

@Service
public class LoanAdapter implements LoanGateway {

    private final CustomLogger logger;
    private final WebClient apiConnectClient;
    private final LoanApisProperties apisProperties;
    private final ITemplateOperations templateOperations;

    public LoanAdapter(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                       final CustomLogger logger,
                       final LoanApisProperties apisProperties,
                       final ITemplateOperations templateOperations) {
        this.logger = logger;
        this.apiConnectClient = apiConnectClient;
        this.apisProperties = apisProperties;
        this.templateOperations = templateOperations;
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
        // TODO: AQUI LLAMAR AL API DE ENVIO DE CORREO
        return Mono.just("SENT");
    }

    private String getApplicationStatus(Map<String, Object> response) {
        Integer status = CreateApplicationResponseUtil.getCodeResponse(response);
        return (status == HttpStatus.SC_OK) ? ApplicationStatus.SUCCESSFUL.name() : ApplicationStatus.ERROR.name();
    }


}
