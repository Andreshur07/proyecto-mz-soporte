package com.marz.soporte.security;

import com.marz.soporte.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expiration;
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }
    public String generarToken(Usuario usuario) {
        Instant now = Instant.now();
        return Jwts.builder().subject(usuario.getCorreo())
                .claim("usuarioId", usuario.getId()).claim("rol", usuario.getRol().name())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusMillis(expiration)))
                .signWith(key).compact();
    }
    public String obtenerCorreo(String token) { return claims(token).getSubject(); }
    public boolean esValido(String token, String correo) {
        Claims claims = claims(token);
        return correo.equalsIgnoreCase(claims.getSubject()) && claims.getExpiration().after(new Date());
    }
    private Claims claims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
