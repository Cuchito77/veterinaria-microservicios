package com.veterinaria.login.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// ═══════════════════════════════════════════════════
// JwtService: emite y valida tokens JWT (HS256).
// En ms-login ademas GENERA el token al hacer login.
// La clave (jwt.secret) es compartida por los 4 MS.
// ═══════════════════════════════════════════════════

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Genera un token firmado con el username (subject) y el rol como claim.
    public String generarToken(String username, String rol) {
        Date ahora = new Date();
        Date vencimiento = new Date(ahora.getTime() + expiration);
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(getKey())
                .compact();
    }

    public boolean esValido(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false; // firma invalida, token expirado o malformado
        }
    }

    public String extraerUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return parseClaims(token).get("rol", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
