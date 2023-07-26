package com.apigateway.helpers;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Component
public class RoleControllerMappingHelper {

    private static Map<String,Set<String>> roleToControllerMapper = new HashMap<>();
    public static Map<String, Set<String>> getBlogStackAllRoleToControllerMapping(){
        Set<String> demoEndpoints = new LinkedHashSet<>();
        demoEndpoints.add("/v1.0/user/");
        roleToControllerMapper.put("string",demoEndpoints);
        return roleToControllerMapper;
    }
}
