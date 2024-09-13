package com.consubanco.consumer.adapters.document;

import com.consubanco.consumer.services.CustomerApiService;
import com.consubanco.consumer.services.OfferApiService;
import com.consubanco.consumer.services.promoter.PromoterApiService;
import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.PAYLOAD_ERROR;

@Service
public class PayloadDocumentAdapter implements PayloadDocumentGateway {

    private final PromoterApiService promoterApiService;
    private final CustomerApiService customerApiService;
    private final OfferApiService offerApiService;
    private final ITemplateOperations templateOperations;

    public PayloadDocumentAdapter(final PromoterApiService promoterApiService,
                                  final CustomerApiService customerApiService,
                                  final OfferApiService offerApiService,
                                  final ITemplateOperations templateOperations) {
        this.promoterApiService = promoterApiService;
        this.customerApiService = customerApiService;
        this.offerApiService = offerApiService;
        this.templateOperations = templateOperations;
    }

    @Override
    public Mono<Map<String, Object>> getAllData(String processId, AgreementConfigVO agreementConfigVO) {
        return Mono.zip(promoterApiService.getPromoterById(agreementConfigVO.getPromoterId()),
                        customerApiService.customerDataByProcess(processId),
                        offerApiService.activeOfferByProcess(processId),
                        customerApiService.customerBiometricValidation(processId))
                .map(tuple -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("agreement_configuration_data", agreementConfigVO);
                    data.put("promoter_data", tuple.getT1());
                    data.put("customer_data", tuple.getT2());
                    data.put("offer_data", tuple.getT3());
                    data.put("biometric_task_data", tuple.getT4());
                    return data;
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> buildPayload(String template, Map<String, Object> data) {
        return templateOperations.process(template, data, Map.class)
                .map(map -> (Map<String, Object>) map)
                .onErrorMap(throwTechnicalError(PAYLOAD_ERROR));
    }

}
