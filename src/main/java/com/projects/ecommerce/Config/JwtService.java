package com.projects.ecommerce.Config;

import com.projects.ecommerce.Auth.token.Token;
import com.projects.ecommerce.Auth.token.TokenRepo;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.springframework.cache.annotation.Cacheable;

@Service
@Component
public class JwtService {
    private final TokenRepo tokenRepo;
    public JwtService(TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
    }


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
            // Set clock skew tolerance to 7 days (1s 10milliseconds)
            long clockSkewTolerance = 1000 * 60 * 60 * 24 * 7L; //very important to show to display token not valid

            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .setAllowedClockSkewSeconds(clockSkewTolerance / 1000) // Set clock skew tolerance
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Handle token parsing exceptions
            invalidateToken(token);
            throw new RuntimeException("Failed to parse JWT token: " + e.getMessage(), e);
        }
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//    private static final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    public void invalidateToken(String token) {
        Optional<Token> tokenOptional = tokenRepo.findByToken(token);
        tokenOptional.get().setExpired(true);
        tokenOptional.get().setRevoked(true);
        tokenOptional.get().setExpirationDate(LocalDateTime.now());
        tokenRepo.save(tokenOptional.get());
//        revokedTokens.add(token);
    }

    // Function to check if a token is revoked
    public boolean isTokenRevoked(String token) {
        Optional<Token> tokenOptional = tokenRepo.findByToken(token);
        return tokenOptional.get().isRevoked() || tokenOptional.get().isExpired();
//        return revokedTokens.contains(token);
    }
}
