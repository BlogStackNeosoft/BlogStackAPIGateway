package com.apigateway.customproviders;

import com.apigateway.exceptions.BlogStackApiGatewayCustomException;
import com.apigateway.helpers.RolesMappingHelper;
import com.apigateway.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class CustomReactiveManager implements ReactiveAuthenticationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomReactiveManager.class);
    private JwtUtils jwtUtils;
    private RolesMappingHelper rolesMappingHelper;

    @Autowired
    public CustomReactiveManager(JwtUtils jwtUtils,RolesMappingHelper rolesMappingHelper) {
        this.jwtUtils = jwtUtils;
        this.rolesMappingHelper = rolesMappingHelper;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        //authentication object will have token in the principal
        // log.info("Control inside authenticate method");
        String token = (String)authentication.getPrincipal();
        List encryptionList = (List)authentication.getCredentials();
        String userId = (String)encryptionList.get(0);
        ServerWebExchange exchange = (ServerWebExchange) encryptionList.get(1);

        log.info("=====================");
        log.info("Token: {}",token);
        log.info("UserId: {}",userId);
        log.info("exchange: {}",exchange);
        log.info("=====================");

        boolean isValidated = jwtUtils.validateTokenForClaims(token,userId);
        if(isValidated){
            if(this.rolesMappingHelper.filter(exchange))
            return Mono.just(new UsernamePasswordAuthenticationToken(token, null, null));
        }

        else
        throw new BlogStackApiGatewayCustomException("Security Failure");

        return null;
    }

}
