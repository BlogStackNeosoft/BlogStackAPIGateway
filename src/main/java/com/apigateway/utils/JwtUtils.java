package com.apigateway.utils;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import com.apigateway.exceptions.BlogStackApiGatewayCustomException;
import com.apigateway.httpexchange.IUserManagementHttpExchange;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {
    private static final long EXPIRATION_DURATION = BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.TWENTY_FOUR_HOURS;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IUserManagementHttpExchange userManagementHttpExchange;

    private static final String SECRET_KEY = BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.JWT_SIGNATURE_KEY;

    public String getSubject(String token)
    {
        return parsClaims(token).getSubject();
    }

    public Claims parsClaims(String token)
    {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (SignatureException signatureException){
            log.info("SignatureException");
            throw new BlogStackApiGatewayCustomException(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.TOKEN_SIGNATURE_UNVERIFIED);
        }
        catch (ExpiredJwtException expiredJwtException){
            log.info("Expiration-Exception");
            throw new BlogStackApiGatewayCustomException(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.JWT_TOKEN_EXPIRED);
        }


    }
    public Boolean validateToken(String token) {
        final String email = getSubject(token);
        Optional<?> user = null;
        try{
            log.info("Before Asynchronous call");
            ResponseEntity<?> userResponseEntity = this.userManagementHttpExchange.fetchUserById(email);
            log.info("After Asysnchornous call");
            user = (Optional<?>) userResponseEntity.getBody();
            // user = restTemplate.getForEntity("http://localhost:9095/v1.0/user/"+email,Optional.class).getBody();
        }
        catch (ResourceAccessException resourceAccessException){
            log.info("ResourceAccessException");

            throw new BlogStackApiGatewayCustomException(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.SERVICE_UNAVAILABLE);
        }


        // return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        return (user.isPresent() && !isTokenExpired(token));
    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public Date extractClaim(String token, Function<Claims, Date> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
