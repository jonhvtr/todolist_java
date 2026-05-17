package com.jonhvtr.todolist.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonhvtr.todolist.domain.enums.ErrorCode;
import com.jonhvtr.todolist.exception.handler.ProblemDetailFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    public AuthenticationFailureHandlerImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.BAD_CREDENTIALS);
        problemDetail.setType(URI.create("https://api.todolist.com/errors/" + "bad_credentials"));

        String msg = "Bad credentials";
        if(exception instanceof BadCredentialsException) {
            msg = "Invalid Email or password";
        }

        problemDetail.setDetail(msg);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
