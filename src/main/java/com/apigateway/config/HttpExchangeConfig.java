package com.apigateway.config;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import com.apigateway.httpexchange.IUserManagementHttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpExchangeConfig {
    @Bean
    public WebClient webClient(ObjectMapper objectMapper) {
        return WebClient.builder()
                .baseUrl(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.USER_MANAGEMENT_BASE_ENDPOINT)
                .build();
    }
    @Bean
    public IUserManagementHttpExchange iUserManagementHttpExchange(WebClient webClient){
        HttpServiceProxyFactory factory=HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build();
        return factory.createClient(IUserManagementHttpExchange.class);
    }

}
