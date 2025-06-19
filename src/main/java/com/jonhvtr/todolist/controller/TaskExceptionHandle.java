package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.exception.InvalidTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class TaskExceptionHandle {
    @ExceptionHandler(InvalidTask.class)
    public ResponseEntity<Map<String, String>> invalidValue(InvalidTask e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }
}
