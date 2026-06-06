package com.example.PrcureflowBackend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/*
 * JwtService contains all JWT-related logic.
 *
 * It is responsible for:
 * 1. Generating JWT tokens after successful login
 * 2. Extracting email from JWT token
 * 3. Extracting claims from JWT token
 * 4. Checking token expiration
 * 5. Validating token before allowing protected API access
 */
@Service
public class JwtService {

    /*
     * Secret key used to sign and verify JWT tokens.
     *
     * Local:
     * jwt.secret=...
     *
     * Render:
     * JWT_SECRET=...
     *
     * application.properties maps:
     * jwt.secret=${JWT_SECRET}
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /*
     * Token expiration time in milliseconds.
     *
     * Example:
     * 86400000 = 24 hours
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /*
     * Generates JWT token for the logged-in user.
     *
     * The frontend stores this token and sends it later as:
     * Authorization: Bearer <token>
     */
    public String generateToken(User user) {

        /*
         * Claims are extra information stored inside the JWT.
         *
         * We store role inside the token for convenience.
         * The main identity is still the user's email.
         */
        Map<String, Object> claims = new HashMap<>();

        if (user.getRole() != null && user.getRole().getName() != null) {
            claims.put("role", user.getRole().getName().name());
        }

        /*
         * Build and sign the JWT token.
         *
         * This syntax uses the newer JJWT API:
         * - claims()
         * - subject()
         * - issuedAt()
         * - expiration()
         * - signWith()
         */
        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * Extracts user email from JWT token.
     *
     * The email is stored as the JWT subject.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /*
     * Extracts token expiration date.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /*
     * Generic method to extract any claim from JWT token.
     *
     * This avoids repeating parsing logic for every claim.
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /*
     * Extracts all claims from JWT token.
     *
     * This method also verifies the token signature.
     *
     * Important:
     * Your JJWT version uses Jwts.parser(), not Jwts.parserBuilder().
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /*
     * Checks if the token is expired.
     *
     * If expiration date is before current date/time,
     * the token is expired.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /*
     * Validates JWT token.
     *
     * Token is valid only if:
     * 1. Email inside token matches expected user email
     * 2. Token is not expired
     */
    public boolean isTokenValid(String token, String userEmail) {

        String extractedEmail = extractEmail(token);

        return extractedEmail.equals(userEmail) && !isTokenExpired(token);
    }

    /*
     * Creates signing key from jwtSecret.
     *
     * Keys.hmacShaKeyFor requires a strong enough secret.
     * Keep JWT_SECRET long in Render environment variables.
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}