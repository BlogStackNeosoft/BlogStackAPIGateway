package com.apigateway.config;

import com.apigateway.customproviders.CustomReactiveManager;
import com.apigateway.customproviders.CustomSecurityContext;
import com.apigateway.filters.CustomWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private CustomWebFilter customWebFilter;
    @Autowired
    private CustomSecurityContext customeSecurityContext;

    @Autowired
    private CustomReactiveManager authenticationManager;

    @Bean
    protected SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){
        serverHttpSecurity.cors().configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowedHeaders(List.of("*"));
            return configuration;
        });

        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable())


                // .cors((corsSpec -> corsSpec.disable()))
                .authorizeExchange(exchange-> exchange.pathMatchers("/v1.0/authentication/**","/v1.0/role/**").permitAll()

                //.cors((corsSpec -> corsSpec.disable()))
                // .cors((corsSpec -> corsSpec.disable()))

                        .anyExchange().authenticated())
                .formLogin(formLoginSpec -> formLoginSpec.disable());
        // serverHttpSecurity.addFilterAfter(this.customWebFilter, SecurityWebFiltersOrder.AUTHORIZATION);
        serverHttpSecurity.securityContextRepository(this.customeSecurityContext);
        serverHttpSecurity.authenticationManager(this.authenticationManager);
        serverHttpSecurity.exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                ).accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))));

        return serverHttpSecurity.build();
    }
}
