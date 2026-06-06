package com.example.PrcureflowBackend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/*
 * JwtService is responsible for:
 * 1. Generating JWT tokens after successful login
 * 2. Extracting user email from JWT token
 * 3. Validating whether a JWT token is still valid
 */
@Service
public class JwtService {

    /*
     * Reads jwt.secret value from application.properties.
     * This secret is used to sign and verify JWT tokens.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /*
     * Reads jwt.expiration value from application.properties.
     * This controls how long the token remains valid.
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /*
     * Converts the plain jwtSecret string into a SecretKey object.
     * This key is used by JJWT to sign and verify the token.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * Generates a JWT token for a logged-in user.
     *
     * The token contains:
     * - subject: user's email
     * - userId
     * - name
     * - role
     * - issue time
     * - expiration time
     */
    public String generateToken(User user) {

        String roleName = user.getRole() != null
                ? user.getRole().getName().name()
                : null;

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("role", roleName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * Extracts the email from the JWT token.
     *
     * In our JWT, the email is stored as the subject.
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /*
     * Checks whether the token is valid.
     *
     * Currently, we only check whether it is expired or not.
     * Later, we can add extra checks if needed.
     */
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    /*
     * Checks whether the token expiration date is before current time.
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /*
     * Extracts all claims/data from the token.
     *
     * Claims are the information stored inside JWT:
     * email, userId, name, role, expiration, etc.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}