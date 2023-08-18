package com.apigateway.utils;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import com.apigateway.exceptions.BlogStackApiGatewayCustomException;
import com.apigateway.helpers.RestCallsHelper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {
   // private static final long EXPIRATION_DURATION = BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.TWENTY_FOUR_HOURS;
    private RestTemplate restTemplate;

    @Autowired
    public JwtUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getSubject(String token,String jwtSecret)
    {
        log.info("inside get subject method");
        return parsClaims(token, jwtSecret).getSubject();
    }
    public Claims parsClaims(String token,String jwtSecret)
    {
        try {
            log.info("Secret: {}",jwtSecret);
            log.info("Token: {}",token);

            return Jwts.parser()
                    .setSigningKey(jwtSecret)
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
        catch (MalformedJwtException malformedJwtException){
            log.info("Malformed-Exception");
            malformedJwtException.printStackTrace();
            throw new BlogStackApiGatewayCustomException(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.MALFORMED_EXCEPTION);
        }
    }

/*
    public Boolean validateToken(String token) {
        return (!isTokenExpired(token));
    }*/

    public Boolean validateTokenForClaims(String token, String userId){


        String jwtSecret = null;
        try{
            // log.info("Before Asynchronous call");
            // ResponseEntity<?> userResponseEntity = this.userManagementHttpExchange.getUserById(userId);
            // log.info("After Asysnchornous call");
            //user = (Optional<JwtSecretsBean>) userResponseEntity.getBody();
            jwtSecret = RestCallsHelper.userId(userId);
        }
        catch (ResourceAccessException resourceAccessException){
            log.info("ResourceAccessException");
            throw new BlogStackApiGatewayCustomException(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.SERVICE_UNAVAILABLE);
        }

        final String email = getSubject(token,jwtSecret);

        // return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        return (!isTokenExpired(token,jwtSecret));
    }


    private Boolean isTokenExpired(String token, String jwtSecret) {
        return extractExpiration(token,jwtSecret).before(new Date());
    }
    public Date extractExpiration(String token,String jwtSecret) {
        return extractClaim(token, Claims::getExpiration,jwtSecret);
    }


    public Date extractClaim(String token, Function<Claims, Date> claimsResolver,String jwtSecret) {
        final Claims claims = extractAllClaims(token,jwtSecret);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token,String jwtSecretKey) {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody();
    }
}
