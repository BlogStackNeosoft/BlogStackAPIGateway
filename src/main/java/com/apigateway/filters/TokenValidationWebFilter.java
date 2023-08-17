package com.apigateway.filters;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import com.apigateway.exceptions.BlogStackApiGatewayCustomException;
import com.apigateway.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
@Component
@Slf4j
public class TokenValidationWebFilter implements WebFilter {

    private JwtUtils jwtUtils;

    @Autowired
    public TokenValidationWebFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        log.info("Inside token validation filter");
        String userId = exchange.getRequest().getHeaders()
                      .getFirst(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.HTTP_HEADER_USER_ID);

        log.info("user_Id: {}",userId);
        String jwtToken = exchange.getRequest().getHeaders()
                         .getFirst(HttpHeaders.AUTHORIZATION)
                         .substring(7);

        log.info("token: {}",jwtToken);
        Boolean isValidated = this.jwtUtils.validateTokenForClaims(jwtToken,userId);
        if(isValidated)
        return chain.filter(exchange);
        else
            throw new BlogStackApiGatewayCustomException("Unauthorized User");
    }
}
