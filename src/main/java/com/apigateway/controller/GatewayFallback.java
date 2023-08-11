package com.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/fallback")
public class GatewayFallback {

    public static Logger logger = LoggerFactory.getLogger(GatewayFallback.class);

    @GetMapping("/blog-stack")
    public ResponseEntity<?> blogStackIamService(Exception exception) {

        logger.info("e.getMessage() = " + exception.getMessage());
        logger.info("e.getStackTrace() = " + Arrays.toString(exception.getStackTrace()));

        return new ResponseEntity<>(exception, HttpStatus.SERVICE_UNAVAILABLE);
    }
}

