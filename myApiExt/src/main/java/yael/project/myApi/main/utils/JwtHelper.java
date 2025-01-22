package yael.project.myApi.main.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtHelper {

   // private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final String SECRET_KEY_STRING = "YaelIsDefinitelyTheBestAndSheWouldBeTheBestFitForAT&T12345678910";
    private static final byte[] SECRET_KEY_BYTES = SECRET_KEY_STRING.getBytes();
    private static final int MINUTES = 60;

    public static String generateToken(String email, String password, Collection roles) {
        Map<String, Object> map = new HashMap<>();
        map.put("password", password);
        List<String> list = Collections.singletonList(roles.stream().toList().get(0).toString());
        map.put("roles", list);
        var now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claims(map)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY_BYTES))
                .compact();
    }

    public static List<String> getRoles(String token) {
        return (List<String>) getTokenBody(token).get("roles");
    }

    public static String extractUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private static Claims getTokenBody(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY_BYTES))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) { // Invalid signature or expired token
            throw new AccessDeniedException("Access denied: " + e.getMessage());
        }
    }

    private static boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }
}
