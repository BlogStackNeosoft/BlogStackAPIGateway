package com.apigateway.utils;

import com.apigateway.commons.BlogStackApiGatewayCommons;
import com.apigateway.exceptions.BlogStackApiGatewayCustomException;
import com.apigateway.httpexchange.IUserManagementHttpExchange;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    // private static final String SECRET_KEY = BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.JWT_SIGNATURE_KEY;
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
            log.info("Before Asynchronous call");
            // ResponseEntity<?> userResponseEntity = this.userManagementHttpExchange.getUserById(userId);
            log.info("After Asysnchornous call");
            //user = (Optional<JwtSecretsBean>) userResponseEntity.getBody();
             LinkedHashMap map = (LinkedHashMap) restTemplate.getForEntity(
                                                BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.USER_MANAGEMENT_GET_BY_ID_URL+userId,
                                                    Optional.class).getBody().get();

            LinkedHashMap data = (LinkedHashMap) map.get(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.MAP_KEY_DATA);
             jwtSecret = (String)data.get(BlogStackApiGatewayCommons.API_GATEWAY_COMMONS.SECRET_JSON_VALUE);
            //jwtSecret = (String) status.get("bsuJwtSecret");
            log.info("Secret: {}",jwtSecret);

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
