package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.domain.dto.ClientAuth;
import com.jonhvtr.todolist.domain.dto.ClientRequest;
import com.jonhvtr.todolist.domain.dto.ClientResponse;
import com.jonhvtr.todolist.domain.dto.TokenJWT;
import com.jonhvtr.todolist.exception.client.ClientEmailAlreadyExistsException;
import com.jonhvtr.todolist.service.ClientService;
import com.jonhvtr.todolist.utils.ClientUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientController.class,
        excludeAutoConfiguration =  SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE))
class ClientControllerTest {
    @MockitoBean
    private ClientService clientService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void login_ShouldReturn200AndToken_WhenCredentialsAreValid() throws Exception {
        TokenJWT jwt = new TokenJWT("fake-jwt-token");
        String json = """
                {
                "email": "email@gmail.com",
                "password": "123456"
                }
                """;

        when(clientService.authenticateClient(any(ClientAuth.class)))
                .thenReturn(jwt);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenJWT").exists())
                .andExpect(jsonPath("$.tokenJWT").isNotEmpty());

        ArgumentCaptor<ClientAuth> clientAuthCaptor = ArgumentCaptor.forClass(ClientAuth.class);

        verify(clientService).authenticateClient(clientAuthCaptor.capture());

        ClientAuth clientAuth = clientAuthCaptor.getValue();

        assertEquals("email@gmail.com", clientAuth.email());
        assertEquals("123456", clientAuth.password());
    }

    @Test
    @WithMockUser
    void login_ShouldReturn400_WhenPasswordIsInvalid() throws Exception {
        String json = """
                {
                "name": "username",
                "email": "email@test.com",
                "password": "12345"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void login_ShouldReturn400_WhenPasswordIsBlank() throws Exception {
        String json = """
                {
                "name": "username",
                "email": "email@test.com",
                "password": ""
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void register_ShouldReturn201AndClient_WhenCredentialsAreValid() throws Exception {
        ClientResponse clientResponse = ClientUtils.clientResponse(ClientUtils.defaultCreate());
        String json = """
                {
                "name": "username",
                "email": "email@test.com",
                "password": "12345678"
                }
                """;

        when(clientService.createUser(any(ClientRequest.class)))
                .thenReturn(clientResponse);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(clientService).createUser(any(ClientRequest.class));
    }


    @Test
    @WithMockUser
    void register_ShouldReturn409_WhenEmailAlreadyExists() throws Exception {
        String json = """
                {
                "name": "username",
                "email": "email@test.com",
                "password": "12345678"
                }
                """;

        when(clientService.createUser(any(ClientRequest.class)))
                .thenThrow(new ClientEmailAlreadyExistsException());

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Email already exists!"));
    }


    @Test
    @WithMockUser
    void register_ShouldReturn400_WhenNameIsBlank() throws Exception {
        String json = """
                {
                "name": "",
                "email": "email@test.com",
                "password": "12345678"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void register_ShouldReturn400_WhenEmailIsBlank() throws Exception {
        String json = """
                {
                "name": "username",
                "email": "",
                "password": "12345678"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("Email is required."));
    }

    @Test
    @WithMockUser
    void register_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        String json = """
                {
                "name": "username",
                "email": "invalid-email",
                "password": "12345678"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("Invalid E-mail."));
    }

    @Test
    @WithMockUser
    void register_ShouldReturn400_WhenPasswordIsBlank() throws Exception {
        String json = """
                {
                "name": "username",
                "email": "email@test.com",
                "password": ""
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void register_ShouldReturn400_WhenPasswordIsInvalid() throws Exception {
        String json = """
                {
                "name": "username",
                "email": "email@test.com",
                "password": "12345"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
