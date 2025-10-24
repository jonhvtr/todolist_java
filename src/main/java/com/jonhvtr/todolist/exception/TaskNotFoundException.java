package com.jonhvtr.todolist.exception;

import lombok.Getter;

@Getter
public class TaskNotFoundException extends TodoListException {
    private final Long taskId;

    public TaskNotFoundException(Long taskId) {
        super("Task com ID " + taskId + " não encontrado.");
        this.taskId = taskId;
    }
}
