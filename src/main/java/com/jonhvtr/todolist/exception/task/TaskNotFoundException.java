package com.jonhvtr.todolist.exception.task;

import com.jonhvtr.todolist.domain.enums.ErrorCode;
import com.jonhvtr.todolist.exception.TodoListException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskNotFoundException extends TodoListException {
    private final ErrorCode errorCode;
    private final UUID taskId;

    public TaskNotFoundException(ErrorCode errorCode, UUID taskId) {
        super("Task com ID " + taskId + " não encontrado.");
        this.taskId = taskId;
        this.errorCode = errorCode;
    }
}
