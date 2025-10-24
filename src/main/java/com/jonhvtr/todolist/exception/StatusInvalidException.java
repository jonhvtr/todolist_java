package com.jonhvtr.todolist.exception;

import com.jonhvtr.todolist.domain.enums.Status;
import lombok.Getter;

@Getter
public class StatusInvalidException extends TodoListException {
    private final Status status;

    public StatusInvalidException(Status status) {
        super("Status - " + status.name() + " inválido");
        this.status = status;
    }
}
