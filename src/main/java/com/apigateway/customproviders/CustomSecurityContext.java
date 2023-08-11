package com.apigateway.customproviders;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomSecurityContext implements ServerSecurityContextRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSecurityContext.class);
    @Autowired
    ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.NOT_SUPPORTED_YET);
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        LOGGER.info("Inside load method ::");
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.BEARER))
                .flatMap(authHeader -> {
                    String token = authHeader.substring(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.INDEX_SEVEN);
                    Authentication auth = new UsernamePasswordAuthenticationToken(token,token);
                    log.info("Before executing authenticate method");
                    return this.reactiveAuthenticationManager.authenticate(auth)
                            .switchIfEmpty(Mono.error(new Exception(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.INVALID_AUTHENTICATION_TOKEN)))
                            .map(SecurityContextImpl::new);
                });
    }
}
