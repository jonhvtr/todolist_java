package com.jonhvtr.todolist.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonhvtr.todolist.domain.dto.ClientAuth;
import com.jonhvtr.todolist.domain.entities.Client;
import com.jonhvtr.todolist.domain.entities.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Client client = ((UserDetailsImpl) authResult.getPrincipal()).getClient();

        String token = tokenService.generateToken(
                client.getId(),
                client.getEmail(),
                client.getRoles()
        );

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(objectMapper.writeValueAsString(Map.of("AccessToken", token)));
    }

    public JsonAuthenticationFilter(
            AuthenticationManager authenticationManager,
            AuthenticationFailureHandler authenticationFailureHandler,
            ObjectMapper objectMapper, TokenService tokenService) {
        this.tokenService = tokenService;
        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(authenticationFailureHandler);
        setFilterProcessesUrl("/auth/login");
        setAllowSessionCreation(false);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            ClientAuth clientAuth = objectMapper.readValue(request.getInputStream(), ClientAuth.class);

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(
                            clientAuth.email(),
                            clientAuth.password());

            return getAuthenticationManager().authenticate(authRequest);
        } catch (IOException ex) {
            throw new BadCredentialsException("Invalid login payload");
        }
    }
}
