package com.apigateway.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class BlogStackApiGatewayCustomException extends RuntimeException {
    public BlogStackApiGatewayCustomException() {
    }

    public BlogStackApiGatewayCustomException(String message) {
        super(message);
    }

    public BlogStackApiGatewayCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
