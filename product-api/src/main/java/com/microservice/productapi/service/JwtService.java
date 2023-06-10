package com.microservice.productapi.service;

import com.microservice.productapi.dto.jwt.JwtResponse;
import com.microservice.productapi.exception.AuthenticationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class JwtService {

    private static final String EMPTY = " ";
    private static final Integer TOKEN_INDEX = 1;

    @Value("${api-config.secrets.api-secret}")
    private String apiSecret;

    public void isAuthorized(String token) {
        var accessToken = extractToken(token);
        try {
            var claims = Jwts
                    .parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(apiSecret.getBytes()))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            var user = JwtResponse.getUser(claims);
            if(isEmpty(user) || isEmpty(user.getId())) {
                throw new AuthenticationException("The user is not valid!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Error while trying to process the access token!");
        }
    }

    private String extractToken(String token) {
        if(isEmpty(token)) {
            throw new AuthenticationException("The access token was not informed!");
        }
        if(token.toLowerCase().contains(EMPTY)) {
            token = token.split(EMPTY)[TOKEN_INDEX];
        }
        return token;
    }
}
