package com.cims.api.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * The JwtUtil class provides utility methods for working with JSON Web Tokens (JWTs).
 * It allows for validating a JWT's signature and extracting its claims.
 */
@Component
public class JwtUtil {

    private static final String SECRET = "5971337436773979244226452948404D635166546A576E5A7234753778214125";

    /**
     * Validates a JSON Web Token's signature.
     *
     * @param token the JWT to validate
     */
    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    /**
     * Returns the signing key used to verify the JWT signature.
     *
     * @return the signing key as a Key object
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
