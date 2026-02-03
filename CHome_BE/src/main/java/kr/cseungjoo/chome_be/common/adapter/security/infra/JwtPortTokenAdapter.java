package kr.cseungjoo.chome_be.common.adapter.security.infra;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.cseungjoo.chome_be.common.port.out.TokenProviderPort;
import kr.cseungjoo.chome_be.user.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtPortTokenAdapter implements TokenProviderPort {

    private final SecretKey secretKey;
    private final long accessExpirationMs;

    public JwtPortTokenAdapter(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-expiration-ms:1800000}") long accessExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
    }

    @Override
    public String issue(Long userId, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Long resolve(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    @Override
    public Role resolveRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = claims.get("role", String.class);
        return Role.valueOf(role);
    }

    @Override
    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
