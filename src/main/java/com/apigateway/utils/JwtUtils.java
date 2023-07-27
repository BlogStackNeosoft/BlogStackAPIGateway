package com.apigateway.utils;

import com.apigateway.exceptions.BlogStackApiGatewayCustomException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {


    private static final long EXPIRATION_DURATION = 20 * 60 * 60 * 1000; //24 HOURS

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final String SECRET_KEY = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

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
            throw new BlogStackApiGatewayCustomException(HttpStatusCode.valueOf(401),"JWT signature does not match locally computed signature");
        }
        catch (ExpiredJwtException expiredJwtException){
            throw new BlogStackApiGatewayCustomException(HttpStatusCode.valueOf(401),"JWT token has expired");
        }


    }
    public Boolean validateToken(String token) {
        final String email = getSubject(token);
        Optional<?> user = null;
        try{
            user = restTemplate.getForEntity("http://localhost:9091/v1.0/user/"+email,Optional.class).getBody();
        }
        catch (ResourceAccessException resourceAccessException){
            log.info("ResourceAccessException");
            throw new BlogStackApiGatewayCustomException(HttpStatusCode.valueOf(503),"The Service is temporarily unavailable");
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
