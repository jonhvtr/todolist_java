package com.jonhvtr.todolist.exception.client;

import com.jonhvtr.todolist.exception.TodoListException;

public class ClientEmailAlreadyExistsException extends TodoListException {
    public ClientEmailAlreadyExistsException() {
        super("Email already exists!");
    }
}
