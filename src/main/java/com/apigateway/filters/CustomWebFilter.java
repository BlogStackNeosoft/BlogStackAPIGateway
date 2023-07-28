package com.apigateway.filters;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import com.apigateway.exceptions.BlogStackApiGatewayCustomException;
import com.apigateway.helpers.RoleControllerMappingHelper;
import com.apigateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class CustomWebFilter implements WebFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @SneakyThrows(Exception.class)
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String uri = exchange.getRequest().getURI().getPath();

        String jwtToken = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION)
                .substring(7);

        Claims claims = this.jwtUtils.parsClaims(jwtToken);
        List<String> tokenEncryptedRoles = (List) claims.get(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.CLAIM_EXTRACTION_KEY);

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
                            /*log.info("The control has reached till the inner code of incrementing the counter");*/
                            count++;
                            break BLOGSTACK_OUTER_WHILE_LOOP;
                        }
                    }
                }
            }
        }

        if(count == 0){
            log.info("unauthorized exception");
            throw new BlogStackApiGatewayCustomException(HttpStatusCode.valueOf(401),new ResponseStatusException(HttpStatusCode.valueOf(401),"The user is not authorized to access the resource").getReason());
        }


        return chain.filter(exchange);
    }
}
