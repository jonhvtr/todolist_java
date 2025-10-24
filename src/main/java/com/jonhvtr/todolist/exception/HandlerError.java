package com.jonhvtr.todolist.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class HandlerError {

    @Order(1)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] NoHandlerFoundException - path={} - method={}", traceId, request.getRequestURI(), request.getMethod());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("https://api.todolist.com/errors/endpoint-not-found"));
        problemDetail.setTitle("Endpoint não encontrado");
        problemDetail.setDetail("O endpoint " + request.getRequestURI() + " não existe");
        problemDetail.setProperty("app:errorCode", "END-404");
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", traceId);
        return problemDetail;
    }

    @Order(2)
    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleTaskNotFound(TaskNotFoundException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] TaskNotFoundException - taskId={} - path={} - {}",
                traceId, ex.getTaskId(), request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("https://api.todolist.com/tasks/task-not-found"));
        problemDetail.setTitle("Task não encontrada.");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("app:errorCode", "TSK-404");
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("taskId", ex.getTaskId());
        return problemDetail;
    }

    @Order(3)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErros(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create("https://api.todolist.com/errors/validation"));
        problemDetail.setTitle("Dados inválidos");

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("[{}] Validation error - path={} - fields={}", traceId, request.getRequestURI(), fieldErrors);

        problemDetail.setDetail("Erro de validação nos campos enviados.");
        problemDetail.setProperty("app:errorCode", "VAL-400");
        problemDetail.setProperty("errors", fieldErrors);
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @Order(4)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] IllegalArgumentException - path={} - {}", traceId, request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create("https://api.todolist.com/errors/illegal-argument"));
        problemDetail.setTitle("Requisição inválida");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("app:errorCode", "ARG-400");
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @Order(5)
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();

        Map<String, String> invalidParams = ex.getConstraintViolations().stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage,
                                (msg1, msg2) -> msg1
                        ));

        log.warn("[{}] ConstraintViolation - path={} - message={}", traceId, request.getRequestURI(), invalidParams);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create("https://api.todolist.com/errors/validation-error"));
        problemDetail.setTitle("Erro de validação nos parâmetros");
        problemDetail.setDetail("Um ou mais parâmetros da requisição são inválidos.");
        problemDetail.setProperty("app:errorCode", "VAL-400");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("traceId", traceId);
        problemDetail.setProperty("invalidParams", invalidParams);
        return problemDetail;
    }

    @Order(99)
    @ExceptionHandler(TodoListException.class)
    public ProblemDetail handleGeneric(TodoListException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] Unexpected error - path={} - message={}", traceId, request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setType(URI.create("https://api.todolist.com/errors/internal"));
        problemDetail.setTitle("Erro interno no servidor");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("app:errorCode", "GEN-500");
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
