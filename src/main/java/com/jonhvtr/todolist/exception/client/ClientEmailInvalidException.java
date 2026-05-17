package com.jonhvtr.todolist.exception.client;

public class ClientEmailInvalidException extends RuntimeException {
    public ClientEmailInvalidException() {
        super("Invalid E-mail!");
    }
}
