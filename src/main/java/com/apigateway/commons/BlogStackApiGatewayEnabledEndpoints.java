package com.apigateway.commons;

public enum BlogStackApiGatewayEnabledEndpoints {
    BLOG_STACK_ENABLED_ENDPOINTS;
    public static final String AUTHENTICATION_CONTROLLER_SIGN_IN = "/v1.0/authentication/sign-in/";
    public static final String AUTHENTICATION_CONTROLLER_SIGN_UP = "/v1.0/authentication/sign-up/";
    public static final String AUTHENTICATION_CONTROLLER_REFRESH_TOKEN = "/v1.0/authentication/refresh-token/";

    public static final String USER_CONTROLLER_FORGOT_PASSWORD = "/v1.0/user/forgot-password/";
    public static final String USER_CONTROLLER_VALIDATE_OTP = "/v1.0/user/validate-otp/";
    public static final String USER_CONTROLLER_RESET_PASSWORD = "/v1.0/user/reset-password/";

}
