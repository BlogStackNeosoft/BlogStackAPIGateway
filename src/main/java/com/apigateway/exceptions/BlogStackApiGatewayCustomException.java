package com.apigateway.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class BlogStackApiGatewayCustomException extends ResponseStatusException {



      public BlogStackApiGatewayCustomException(HttpStatusCode status, String reason) {
        super(status, reason);
     }

}
