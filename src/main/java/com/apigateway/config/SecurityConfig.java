package com.apigateway.config;

import com.apigateway.commons.BlogStackApiGatewayEnabledEndpoints;
import com.apigateway.customproviders.CustomReactiveManager;
import com.apigateway.customproviders.CustomSecurityContext;
import com.apigateway.filters.RoleAuthorizationWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private RoleAuthorizationWebFilter roleAuthorizationWebFilter;
    @Autowired
    private CustomSecurityContext customeSecurityContext;

    @Autowired
    private CustomReactiveManager authenticationManager;

    @Bean
    protected SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){

        // code to allow cors
        serverHttpSecurity.cors().configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowedHeaders(List.of("*"));
            return configuration;
        });

        // code to add a custom filter to run before Security filter chain
        serverHttpSecurity.addFilterBefore(roleAuthorizationWebFilter, SecurityWebFiltersOrder.FIRST);

        // disable cross site request forgery
        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable());

        // securing the required endpoints and enabling the required endpoints
        serverHttpSecurity.authorizeExchange(exchange-> exchange.pathMatchers(
                                BlogStackApiGatewayEnabledEndpoints.AUTHENTICATION_CONTROLLER_SIGN_IN,
                                BlogStackApiGatewayEnabledEndpoints.AUTHENTICATION_CONTROLLER_SIGN_UP,
                                BlogStackApiGatewayEnabledEndpoints.AUTHENTICATION_CONTROLLER_REFRESH_TOKEN,
                                BlogStackApiGatewayEnabledEndpoints.USER_CONTROLLER_VALIDATE_OTP,
                                BlogStackApiGatewayEnabledEndpoints.USER_CONTROLLER_FORGOT_PASSWORD,
                                BlogStackApiGatewayEnabledEndpoints.USER_CONTROLLER_RESET_PASSWORD
                        ).permitAll()
                        .anyExchange().authenticated())
                .formLogin(formLoginSpec -> formLoginSpec.disable());


        // Supplying the securitycontext repository
        serverHttpSecurity.securityContextRepository(this.customeSecurityContext);

        // Supplying the authentication manager to authenticate the token
        serverHttpSecurity.authenticationManager(this.authenticationManager);

        serverHttpSecurity.exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                ).accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))));

        return serverHttpSecurity.build();
    }
}
