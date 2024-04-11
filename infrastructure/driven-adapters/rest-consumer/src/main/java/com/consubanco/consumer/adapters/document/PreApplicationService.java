package com.consubanco.consumer.adapters.document;

import com.consubanco.consumer.adapters.document.properties.PayloadApisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PreApplicationService {

    private final WebClient apiConnectClient;
    private final PayloadApisProperties apis;

    public PreApplicationService(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                                 final PayloadApisProperties apisProperties) {
        this.apiConnectClient = apiConnectClient;
        this.apis = apisProperties;
    }


}
