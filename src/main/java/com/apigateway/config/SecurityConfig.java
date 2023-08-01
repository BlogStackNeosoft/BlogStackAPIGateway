package com.apigateway.config;

import com.apigateway.customproviders.CustomReactiveManager;
import com.apigateway.customproviders.CustomSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private CustomSecurityContext customeSecurityContext;

    @Autowired
    private CustomReactiveManager authenticationManager;

    @Bean
    protected SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){
        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable())
                .cors((corsSpec -> corsSpec.disable()))
                .authorizeExchange(exchange-> exchange.pathMatchers("/api/demo","/api/demo/login","/api/demo/auth/refresh","/authentication/**","/fallback/**").permitAll()
                        .anyExchange().authenticated())
                .formLogin(formLoginSpec -> formLoginSpec.disable());

        serverHttpSecurity.securityContextRepository(this.customeSecurityContext);
        serverHttpSecurity.authenticationManager(this.authenticationManager);
        serverHttpSecurity.exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                ).accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))));

        return serverHttpSecurity.build();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
