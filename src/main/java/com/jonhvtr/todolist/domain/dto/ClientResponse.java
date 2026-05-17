package com.jonhvtr.todolist.domain.dto;

import com.jonhvtr.todolist.domain.entities.Client;

import java.util.UUID;

public record ClientResponse(
        UUID id,
        String email
) {

    public ClientResponse(Client client) {
        this(client.getId(), client.getEmail());
    }
}
