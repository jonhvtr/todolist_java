package com.jonhvtr.todolist.exception.task;

import com.jonhvtr.todolist.domain.enums.Status;
import com.jonhvtr.todolist.exception.TodoListException;
import lombok.Getter;

@Getter
public class StatusInvalidException extends TodoListException {
    private final Status status;

    public StatusInvalidException(Status status) {
        super("Status - " + status.name() + " inválido");
        this.status = status;
    }
}
