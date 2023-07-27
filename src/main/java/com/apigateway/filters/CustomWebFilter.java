package com.apigateway.filters;

import com.apigateway.exceptions.BlogStackUnauthorizedEndpointAccessException;
import com.apigateway.helpers.RoleControllerMappingHelper;
import com.apigateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
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
        log.info("The request path intercepted in webfiletr: "+exchange.getRequest().getURI().getPath());
        String jwtToken = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION)
                .substring(7);

        Claims claims = this.jwtUtils.parsClaims(jwtToken);
        List<String> roles = (List) claims.get("roles");

        log.info("Printing the roles decrypted from the token:");
        log.info(String.format("List: %s ",roles));

        Map<String, Set<String>> blogStackAllRoleToControllerMapping =  RoleControllerMappingHelper.getBlogStackAllRoleToControllerMapping();
        int count = 0;

        Set<String> blogStackRoles = blogStackAllRoleToControllerMapping.keySet();
        Iterator<String> blogStackRolesIterator = blogStackRoles.iterator();

        BLOGSTACK_OUTER_WHILE_LOOP: while(blogStackRolesIterator.hasNext()){
            String currentRole = blogStackRolesIterator.next();
            if(currentRole.equals(roles.get(0))){
                Set<String> blogStackRoleEndpoints = blogStackAllRoleToControllerMapping.get(currentRole);
                Iterator<String> blogStackEndponitsIterator = blogStackRoleEndpoints.iterator();
                while(blogStackEndponitsIterator.hasNext()){
                    if(blogStackEndponitsIterator.next().equals(uri))
                    {
                        count++;
                        break BLOGSTACK_OUTER_WHILE_LOOP;
                    }
                }
            }
        }

        if(count == 0)
            throw new BlogStackUnauthorizedEndpointAccessException(HttpStatusCode.valueOf(401),"The user is unauthorized to access the resource endpoint");

        return chain.filter(exchange);
    }
}
