package com.jonhvtr.todolist.exception;

import com.jonhvtr.todolist.domain.enums.Priority;
import lombok.Getter;

@Getter
public class PriorityInvalidException extends RuntimeException {
    private final Priority priority;

    public PriorityInvalidException(Priority priority) {
        super("Prioridade - " + priority.name() + " inválida.");
        this.priority = priority;
    }

}
