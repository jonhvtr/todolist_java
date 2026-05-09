package com.jonhvtr.todolist.exception.task;

import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.exception.TodoListException;
import lombok.Getter;

@Getter
public class PriorityInvalidException extends TodoListException {
    private final Priority priority;

    public PriorityInvalidException(Priority priority) {
        super("Prioridade - " + priority.name() + " inválida.");
        this.priority = priority;
    }

}
