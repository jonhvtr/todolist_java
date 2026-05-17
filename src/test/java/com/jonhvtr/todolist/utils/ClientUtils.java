package com.jonhvtr.todolist.utils;

import com.jonhvtr.todolist.domain.dto.ClientRequest;
import com.jonhvtr.todolist.domain.dto.ClientResponse;
import com.jonhvtr.todolist.domain.entities.Client;

import java.util.UUID;

public class ClientUtils {
    private static final UUID CLIENT_ID = UUID.randomUUID();

    public static ClientRequest clientRequest() {
        return new ClientRequest("username", "email@test.com", "1234567");
    }

    public static Client defaultClient() {
        return Client.builder()
                .id(CLIENT_ID)
                .name("username")
                .email("email@test.com")
                .password("1234567")
                .build();
    }

    public static Client defaultCreate() {
        return Client.builder()
                .name("username")
                .email("email@test.com")
                .password("1234567")
                .build();
    }

    public static ClientResponse clientResponse(Client data) {
        return new ClientResponse(data.getId(), data.getEmail());
    }
}
