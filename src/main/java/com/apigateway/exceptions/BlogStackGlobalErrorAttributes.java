package com.apigateway.exceptions;

import com.apigateway.enums.ErrorAttributeKey;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


record ExceptionRule(Class<?> exceptionClass, HttpStatus status){}
@Component
public class BlogStackGlobalErrorAttributes extends DefaultErrorAttributes {

    private final List<ExceptionRule> exceptionRules = List.of(
            new ExceptionRule(BlogStackApiGatewayCustomException.class,HttpStatus.UNAUTHORIZED)
    );

    @Override
    public Map<String,Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options){

        Throwable error = getError(request);

        final String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        Optional<ExceptionRule> exceptionRuleOptional = exceptionRules.stream()
                .map(exceptionRule -> exceptionRule.exceptionClass().isInstance(error) ? exceptionRule : null)
                .filter(Objects::nonNull)
                .findFirst();

        return exceptionRuleOptional.<Map<String,Object>>map(exceptionRule -> Map.of(ErrorAttributeKey.CODE.getKey(),exceptionRule.status().value(),ErrorAttributeKey.MESSAGE.getKey(),error.getMessage(),ErrorAttributeKey.TIME.getKey(), timeStamp))
                .orElseGet(() -> Map.of(ErrorAttributeKey.CODE.getKey(), determineHttpStatus(error).value(),ErrorAttributeKey.MESSAGE.getKey(),error.getMessage(),ErrorAttributeKey.TIME.getKey(), timeStamp));
    }

    private HttpStatus determineHttpStatus(Throwable error){
        return error instanceof ResponseStatusException err ? (HttpStatus) err.getStatusCode() : MergedAnnotations.from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class).getValue(ErrorAttributeKey.CODE.getKey(), HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
