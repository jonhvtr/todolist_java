package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.domain.dto.ClientAuth;
import com.jonhvtr.todolist.domain.dto.ClientRequest;
import com.jonhvtr.todolist.domain.dto.ClientResponse;
import com.jonhvtr.todolist.domain.dto.TokenJWT;
import com.jonhvtr.todolist.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenJWT> login(@Valid @RequestBody ClientAuth clientAuth) {
        TokenJWT tokenJWT = clientService.authenticateClient(clientAuth);
        return ResponseEntity.ok(tokenJWT);
    }

    @PostMapping("/register")
    public ResponseEntity<ClientResponse> register(@Valid @RequestBody ClientRequest dto) {
        ClientResponse newUser = clientService.createUser(dto);
        URI uri = URI.create("/login/" + newUser.id());
        return ResponseEntity.created(uri).body(newUser);
    }
}
