package com.apigateway.advisor;

import com.apigateway.beans.response.ServiceResponseBean;
import com.apigateway.commons.BlogStackApiGatewayCommons;
import com.apigateway.exceptions.BlogStackUnauthorizedEndpointAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Component
public class BlogStackControllerAdvice extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BlogStackUnauthorizedEndpointAccessException.class)
    Mono<ServiceResponseBean> unauthorizedExceptionHandler(ServerWebExchange serverWebExchange, BlogStackUnauthorizedEndpointAccessException unauthorizedEndpointAccessException){
        return Mono.just(ServiceResponseBean.builder()
                .message("Unauhorized access to the resource")
                .status(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.STATUS_FALSE)
                .build());
    }

}
