package com.cims.auth.service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtService class provides methods for generating and validating JWT tokens using a secret key.
 */
@Component
public class JwtService {

    /**
     * Secret key used for signing and verifying JWT tokens.
     */
    private static final String SECRET = "5971337436773979244226452948404D635166546A576E5A7234753778214125";

    /**
     * Validates the given JWT token by parsing its claims and verifying its signature.
     * Throws an exception if the token is invalid or expired.
     *
     * @param token the JWT token to validate
     */
    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    /**
     * Generates a JWT token for the given user name and sets its expiration time to 30 minutes from the current time.
     *
     * @param userName the name of the user for whom the token is generated
     * @return the JWT token as a string
     */
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    /**
     * Creates a JWT token with the given claims and user name, and signs it with the secret key using the HS256 algorithm.
     *
     * @param claims   the claims to include in the token
     * @param userName the name of the user for whom the token is created
     * @return the JWT token as a string
     */
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Decodes the secret key from base64 and returns it as a Key object for use in signing and verifying JWT tokens.
     *
     * @return the secret key as a Key object
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
