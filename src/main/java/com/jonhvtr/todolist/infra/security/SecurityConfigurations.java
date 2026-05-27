package com.jonhvtr.todolist.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonhvtr.todolist.domain.dto.RouteConfig;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(ObjectMapper objectMapper) {
        return new AuthenticationFailureHandlerImpl(objectMapper);
    }

    @Bean
    public JsonAuthenticationFilter jsonAuthenticationFilter(
            AuthenticationManager authenticationManager,
            AuthenticationFailureHandler authenticationFailureHandler,
            ObjectMapper objectMapper,
            TokenService tokenService) {

        return new JsonAuthenticationFilter(
                authenticationManager,
                authenticationFailureHandler,
                objectMapper,
                tokenService
        );
    }

    protected static final RouteConfig[] ENDPOINTS_WITH_AUTHENTICATION_SUPPORTED = {
            new RouteConfig("/tasks", HttpMethod.GET),
            new RouteConfig("/tasks/{taskId}", HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE),
            new RouteConfig("/tasks/calendar", HttpMethod.GET),
            new RouteConfig("/tasks/search", HttpMethod.GET),
            new RouteConfig("/tasks/reminder", HttpMethod.GET),
            new RouteConfig("/tasks/reminder/pending", HttpMethod.GET),
            new RouteConfig("/tasks/reminder/to-send-now", HttpMethod.GET),
            new RouteConfig("/tasks", HttpMethod.POST),
            new RouteConfig("/tasks/{taskId}/complete", HttpMethod.PATCH),
            new RouteConfig("/tasks/{taskId}/due-date", HttpMethod.PATCH),
            new RouteConfig("/tasks/{taskId}/add-reminder", HttpMethod.PATCH),
            new RouteConfig("/tasks/{taskId}/remove-reminder", HttpMethod.PATCH),
    };

    @Bean
    @Order(1)
    public SecurityFilterChain authChain(HttpSecurity http, JsonAuthenticationFilter jsonAuthenticationFilter) throws Exception {
        return http
                .securityMatcher("/auth/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterAt(jsonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain swaggerChain(HttpSecurity http, JsonAuthenticationFilter jsonAuthenticationFilter) throws Exception {
        return http
                .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterAt(jsonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            CustomAuthenticationEntryPoint authEntryPoint
    ) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                            .requestMatchers("/error").permitAll();

                    for (RouteConfig routeConfig : SecurityConfigurations.ENDPOINTS_WITH_AUTHENTICATION_SUPPORTED) {
                        for (HttpMethod method : routeConfig.method()) {
                            authorize.requestMatchers(method, routeConfig.path()).hasAnyRole("CLIENT", "SERVER");
                        }
                    }
                    authorize.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authEntryPoint))
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();

        converter.setAuthoritiesClaimName("roles");
        converter.setAuthorityPrefix("ROLE_");


        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
