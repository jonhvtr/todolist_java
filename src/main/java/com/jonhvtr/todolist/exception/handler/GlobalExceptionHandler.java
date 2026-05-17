package com.jonhvtr.todolist.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jonhvtr.todolist.domain.enums.ErrorCode;
import com.jonhvtr.todolist.exception.TodoListException;
import com.jonhvtr.todolist.exception.client.ClientEmailAlreadyExistsException;
import com.jonhvtr.todolist.exception.key.RsaConversionException;
import com.jonhvtr.todolist.exception.task.TaskNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String BASE_ERROR_URI = "https://api.todolist.com/errors/";

    @Order(1)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();
        log.warn("[{}] NoHandlerFoundException - path={} - method={}", traceId, request.getRequestURI(), request.getMethod());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.PAGE_NOT_FOUND, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "endpoint-not-found"));
        problemDetail.setProperty("traceId", traceId);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @Order(2)
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTaskNotFound(TaskNotFoundException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();
        log.warn("[{}] TaskNotFoundException - taskId={} - path={} - {}",
                traceId, ex.getTaskId(), request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.TASK_NOT_FOUND, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "task-not-found"));
        problemDetail.setProperty("taskId", ex.getTaskId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @Order(3)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleJsonParseError(
            HttpMessageNotReadableException ex, HttpServletRequest request
    ) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] HttpMessageNotReadableException - path={} - method={}", traceId, request.getRequestURI(), request.getMethod());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.INVALID_REQUEST_BODY);

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String field = ife.getPath()
                    .stream().map(JsonMappingException.Reference::getFieldName)
                    .findFirst()
                    .orElse("unknown");

            problemDetail.setDetail("Invalid value for field " + field);
        } else {
            problemDetail.setDetail("Malformed JSON request");
        }

        problemDetail.setType(URI.create(BASE_ERROR_URI + "invalid-request-body"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("traceId", traceId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @Order(4)
    @ExceptionHandler(ClientEmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleClientEmailAlreadyExists(ClientEmailAlreadyExistsException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();
        log.warn("[{}] ClientEmailAlreadyExists - path={}", traceId, request.getRequestURI());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.EMAIL_ALREADY_EXISTS, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "client-email-already-exists"));
        problemDetail.setProperty("traceId", traceId);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @Order(5)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));


        log.warn("[{}] Validation error - path={} - fields={}", traceId, request.getRequestURI(), fieldErrors);

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.VALIDATION_ERROR);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "validation"));
        problemDetail.setDetail("invalid fields");
        problemDetail.setProperty("errors", fieldErrors);
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @Order(6)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();
        log.warn("[{}] IllegalArgumentException - path={} - {}", traceId, request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.INVALID_ARGUMENT, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "illegal-argument"));
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @Order(7)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();

        Map<String, String> invalidParams = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (msg1, msg2) -> msg1
                ));

        log.warn("[{}] ConstraintViolation - path={} - message={}", traceId, request.getRequestURI(), invalidParams);

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.VALIDATION_ERROR, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "violation-error"));
        problemDetail.setProperty("traceId", traceId);
        problemDetail.setProperty("invalidParams", invalidParams);
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @Order(8)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ProblemDetail> handleDatabaseError(DataAccessException ex) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();

        log.warn("[{}] DataAccessException - message={}", traceId, ex.getMessage());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.INTERNAL_ERROR, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "database-error"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problemDetail);
    }

    @Order(9)
    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<ProblemDetail> handleQueryTimeout(QueryTimeoutException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();

        log.warn("[{}] QueryTimeout - path={}", traceId, request.getRequestURI());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.GATEWAY_TIMEOUT, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "query-timeout"));
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(problemDetail);
    }

    @Order(10)
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleInsufficientAuthentication(InsufficientAuthenticationException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();

        log.warn("[{}] InsufficientAuthenticationException - path={}", traceId, request.getRequestURI());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.AUTH_TOKEN_MISSING, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "auth-token-missing"));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @Order(11)
    @ExceptionHandler(RsaConversionException.class)
    public ResponseEntity<ProblemDetail> handleRsaConversion(RsaConversionException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();
        log.warn("[{}] RsaConversion - path={}", traceId, request.getRequestURI());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.RSA_CONVERSION_ERROR, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "rsa-conversion-error"));
        problemDetail.setProperty("key_type", ex.getKeyType());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);

    }

    @Order(99)
    @ExceptionHandler(TodoListException.class)
    public ResponseEntity<ProblemDetail> handleGeneric(TodoListException ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String detail = ex.getMessage();
        log.warn("[{}] Unexpected error - path={} - message={}", traceId, request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetailFactory.from(ErrorCode.INTERNAL_ERROR, detail);
        problemDetail.setType(URI.create(BASE_ERROR_URI + "internal"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
