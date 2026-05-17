package com.jonhvtr.todolist.exception.client;

public class ClientEmailNotFoundException extends RuntimeException {
    public ClientEmailNotFoundException() {
        super("Email not found!");
    }
}
