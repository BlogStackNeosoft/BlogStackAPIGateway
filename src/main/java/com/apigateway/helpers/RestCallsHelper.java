package com.apigateway.helpers;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Optional;

@Component
public class RestCallsHelper {

    private static RestTemplate restTemplate;

    @Autowired
    public RestCallsHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static String userId(String userId){
        LinkedHashMap map = (LinkedHashMap) restTemplate.getForEntity(
                BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.USER_MANAGEMENT_GET_BY_ID_URL+userId,
                Optional.class).getBody().get();

        LinkedHashMap data = (LinkedHashMap) map.get(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.MAP_KEY_DATA);
        return (String)data.get(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.SECRET_JSON_VALUE);
    }
}
