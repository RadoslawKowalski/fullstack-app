package com.example.pasir_kowalski_radoslaw.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    // Stabilny klucz, nie zmienia się po każdym uruchomieniu (ważne!)
    private final Key key = Keys.hmacShaKeyFor("rkowalski-afc6987dcf6sac87fac986cd9z8c76z98f6f99sz8f6s9zc87f69zsd8f7".getBytes());

    // Generowanie tokena na podstawie użytkownika
    public String generateToken(com.example.pasir_kowalski_radoslaw.model.User user) {
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());

        // Czas ważności tokena: 1 godzinę
        long expirationMs = 3600000;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())    // 'sub': kto jest właścicielem tokena
                .setIssuedAt(new Date())    // 'iat': kiedy został wydany
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // 'exp': kiedy wygaśnie
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();    // zakończ budowanie i zwróć token jako String
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // ustawiamy klucz do weryfikacji podpisu
                .build() // JwtParser
                .parseClaimsJws(token) // parsujemy token jako JWS (JSON Web Signature)
                .getBody(); // pobieramy claims (ładunek)
    }

    public String extractUsername(String token) { return extractAllClaims(token).getSubject(); }

    public boolean validateToken(String token) {
        try {
            // jeżeli parsowanie się uda – token poprawny
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            // nieprawidłowy token (np. podpis, wygasł, błędny format)
            return false;
        }
    }
}
