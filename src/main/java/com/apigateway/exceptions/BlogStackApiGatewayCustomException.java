package com.apigateway.exceptions;

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
