package com.apigateway.utils;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Component
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

    private Claims parsClaims(String token)
    {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

    }

    public Boolean validateToken(String token) {
        final String email = getSubject(token);
        LOGGER.info("email ==>"+email);

        Optional<?> user = restTemplate.getForEntity("${blogstack.usermanagement.getbyemail.endpoint}"+email,Optional.class).getBody();
        //return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
