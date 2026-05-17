package com.jonhvtr.todolist.infra.security;

import com.jonhvtr.todolist.domain.entities.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public UUID getUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("id"));
    }

    public String getSubject(Jwt jwt) {
        return jwt.getSubject();
    }

    public String generateToken(UUID clientId, String email, Set<Role> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("TodoList API")
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS))
                .subject(email)
                .claim("id", clientId.toString())
                .claim("roles", roles.stream()
                        .map(r -> r.getRoleName().name())
                        .toList())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
