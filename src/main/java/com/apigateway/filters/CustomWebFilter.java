package com.apigateway.filters;

import com.apigateway.helpers.RoleControllerMappingHelper;
import com.apigateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
        log.info(String.format("List: %s",roles));

        Map<String, Set<String>> blogStackAllRoleToControllerMapping =  RoleControllerMappingHelper.getBlogStackAllRoleToControllerMapping();
        int count = 0;
        Set<String> endpoints = blogStackAllRoleToControllerMapping.get(roles.get(0));
        Iterator<String> iterator = endpoints.iterator();
        while(iterator.hasNext()){
            if(iterator.next().equals(uri)) {
                count++;
                break;
            }
        }
        if(count == 0)
            throw new Exception("The role has failed authorization");

        return chain.filter(exchange);
    }
}
