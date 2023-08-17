package com.apigateway.customproviders;

import com.apigateway.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
@Slf4j
public class CustomReactiveManager implements ReactiveAuthenticationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomReactiveManager.class);
    private JwtUtils jwtUtils;

    @Autowired
    public CustomReactiveManager(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        //authentication object will have token in the principal
        // log.info("Control inside authenticate method");
        String token = (String)authentication.getPrincipal();
       // boolean isValidated = jwtUtils.validateToken(token);
        //if(isValidated)
            return Mono.just(new UsernamePasswordAuthenticationToken(token, null, null));
        // else
       // return null;
    }
}
