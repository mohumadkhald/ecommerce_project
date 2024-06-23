package com.projects.ecommerce.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@Component
public class JwtService {
    private static final String SECRET_KEY = "WmZq3t6weShVmYq3KaPdSgVikmcleckFJefdcdsgvUkXn2r5ucRfUjXnZHMcQfTj";
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsTResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsTResolver.apply(claims);
    }



    public String generateToken(UserDetails userDetails,int expirationDay) {
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationDay))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String jwt)
    {
        jwt = jwt.substring(7);
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(jwt).getBody();
        return String.valueOf(claims.get("sub"));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        boolean isAccountLocked = checkIfAccountLocked(username); // Check if the account is locked

        return !isAccountLocked && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Method to check if the account is locked (you need to implement this)
    private boolean checkIfAccountLocked(String email) {
        // Implement your logic to check if the account is locked
        // This could involve querying your database or any other method to determine the account lock status
        // Return true if the account is locked, false otherwise
        return false;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }





/* ---------------------------------------------------------------------
|||    private Claims extractAllClaims(String token) {
|||       return Jwts
|||               .parserBuilder()
|||               .setSigningKey(getSignInKey())
|||               .build()
|||               .parseClaimsJws(token)
|||                .getBody();
|||        }
--------------------------------------------------------------------------*/



    public Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Handle token parsing exceptions
            // For example, log the error or throw a custom exception
            throw new RuntimeException("Failed to parse JWT token: " + e.getMessage(), e);
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    // Function to invalidate or destroy a token
    public void invalidateToken(String token) {
        revokedTokens.add(token);
    }

    // Function to check if a token is revoked
    public static boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }
}
