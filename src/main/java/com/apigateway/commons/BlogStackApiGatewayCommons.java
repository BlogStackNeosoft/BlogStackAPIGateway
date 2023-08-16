package com.apigateway.commons;

public enum BlogStackApiGatewayCommons {

     API_GATEWAY_COMMONS;
     public final boolean  STATUS_FALSE = false;
     public final String CLAIM_EXTRACTION_KEY = "roles";
     public final String SERVICE_UNAVAILABLE = "The Service is temporarily unavailable";
     public final String JWT_TOKEN_EXPIRED = "JWT token has expired";
     public final String TOKEN_SIGNATURE_UNVERIFIED = "JWT signature does not match locally computed signature";

     public final String MALFORMED_EXCEPTION = "JWT token malformed";
     public final String JWT_SIGNATURE_KEY = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";
     public final String USER_MANAGEMENT_BASE_ENDPOINT = "http://localhost:9095";
     public final Integer TWENTY_FOUR_HOURS = 20 * 60 * 60 * 1000;
     public final String NOT_SUPPORTED_YET = "Not Supported yet";
     public final String INVALID_AUTHENTICATION_TOKEN = "Invalid Authentication Token";
     public final String BEARER = "Bearer ";
     public final Integer INDEX_SEVEN = 7;
     public final String HTTP_HEADER_USER_ID = "userId";

     public final String USER_MANAGEMENT_GET_BY_ID_URL = "http://localhost:9095/v1.0/user/user-id/";
     public final String MAP_KEY_DATA = "data";

     public final String SECRET_JSON_VALUE = "bsuJwtSecret";
}
