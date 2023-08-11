package com.apigateway.response;

import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogStackExceptionHandlingResponse {
    private String message;
    private String code;
    private Instant timeStamp;

}
