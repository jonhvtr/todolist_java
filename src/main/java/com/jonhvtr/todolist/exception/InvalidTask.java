package com.jonhvtr.todolist.exception;

public class InvalidTask extends RuntimeException {
    public InvalidTask(String message) {
        super(message);
    }
}
