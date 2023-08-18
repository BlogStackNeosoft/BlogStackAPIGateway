package com.apigateway.helpers;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import com.apigateway.commons.BlogStackUserManagementServiceBaseEndpoints;
import com.apigateway.exceptions.BlogStackApiGatewayCustomException;
import com.apigateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class RolesMappingHelper {


    private JwtUtils jwtUtils;

    @Autowired
    public RolesMappingHelper(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @SneakyThrows(Exception.class)
    public boolean filter(ServerWebExchange exchange) {
        // exchange.getRequest().getMethod();
        String uri = exchange.getRequest().getURI().getPath();

        if(uri.contains(BlogStackUserManagementServiceBaseEndpoints.AUTHENTICATION_CONTROLLER))
            return true;

        String jwtToken = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION)
                .substring(7);

        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.HTTP_HEADER_USER_ID);

        Claims claims = jwtUtils.parsClaims(jwtToken, RestCallsHelper.userId(userId));
        List<String> tokenEncryptedRoles = (List) claims.get(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.CLAIM_EXTRACTION_KEY);


        //log.info("Roles encrypted in token: {}",tokenEncryptedRoles);

        Map<String, Set<String>> blogStackAllRoleToControllerMapping =  RoleControllerMappingHelper.getBlogStackAllRoleToControllerMapping();
        int count = 0;

        Set<String> blogStackRoles = blogStackAllRoleToControllerMapping.keySet();
        Iterator<String> blogStackRolesIterator = blogStackRoles.iterator();

        BLOGSTACK_OUTER_WHILE_LOOP: while(blogStackRolesIterator.hasNext()){
            int rolesIndex = 0;
            String currentRole = blogStackRolesIterator.next();
            for(int i=0; i < tokenEncryptedRoles.size();i++){
                if(currentRole.equals(tokenEncryptedRoles.get(rolesIndex++))){
                    Set<String> blogStackRoleEndpoints = blogStackAllRoleToControllerMapping.get(currentRole);
                    Iterator<String> blogStackEndponitsIterator = blogStackRoleEndpoints.iterator();
                    while(blogStackEndponitsIterator.hasNext()){
                        if(blogStackEndponitsIterator.next().equals(uri))
                        {
                            log.info("The control has reached till the inner code of incrementing the counter");
                            count++;
                            log.info("Inside count increment");
                            break BLOGSTACK_OUTER_WHILE_LOOP;
                        }
                    }
                }

            }
        }

        if(count == 0){
            log.info("unauthorized exception");
            throw new BlogStackApiGatewayCustomException("The user is not authorized to access the resource");
        }


        return true;
    }
}
