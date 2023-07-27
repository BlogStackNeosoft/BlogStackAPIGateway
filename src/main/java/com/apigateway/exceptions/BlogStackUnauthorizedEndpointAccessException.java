package com.apigateway.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class BlogStackUnauthorizedEndpointAccessException extends ResponseStatusException {

    public BlogStackUnauthorizedEndpointAccessException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

}
