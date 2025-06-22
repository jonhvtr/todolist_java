package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.exception.InvalidTask;
import com.jonhvtr.todolist.exception.TaskException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class TaskExceptionHandle {
    @ExceptionHandler(InvalidTask.class)
    public ResponseEntity<Map<String, String>> invalidValue(InvalidTask e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Erro: ", e.getMessage()));
    }

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<Map<String, String>> handleTaskNotFound(TaskException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Erro: ", e.getMessage()));
    }
}
