package com.jonhvtr.todolist.infra.security;

import com.jonhvtr.todolist.domain.entities.Client;
import com.jonhvtr.todolist.repository.ClientRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils implements ISecurityUtils {
    private final ClientRepository clientRepository;

    private SecurityUtils(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UUID getAuthenticationClientId() {
        Jwt jwt = getJwt();
        return UUID.fromString(jwt.getClaimAsString("id"));
    }

    @Override
    public String getAuthenticationClientEmail() {
        return getJwt().getSubject();
    }

    private Jwt getJwt() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof Jwt jwt)) {

            throw new AccessDeniedException("Unauthenticated");
        }

        return jwt;
    }

    public Client getAuthenticationClient() {
        UUID clientId = getAuthenticationClientId();
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new AccessDeniedException("Client not found"));
    }
}
