package com.apigateway.enums;

import lombok.Getter;

@Getter
public enum ErrorAttributeKey {
    CODE("code"),
    MESSAGE("message"),
    TIME("timestamp");

    private final String key;
    ErrorAttributeKey(String key){
        this.key = key;
    }
}
