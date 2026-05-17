package com.jonhvtr.todolist.domain.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    VALIDATION_ERROR("VLD-400", "Invalid data", HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT("ARG-400", "The value entered is invalid",  HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_BODY("IRB-400", "Invalid request body",  HttpStatus.BAD_REQUEST),
    RSA_CONVERSION_ERROR("RSA-400", "RSA Conversion Error", HttpStatus.BAD_REQUEST),

    PAGE_NOT_FOUND("PAG-404", "Page not found", HttpStatus.NOT_FOUND),
    TASK_NOT_FOUND("TSK-404", "Task not found", HttpStatus.NOT_FOUND),

    EMAIL_ALREADY_EXISTS("CTF-409", "E-mail Already Exists", HttpStatus.CONFLICT),

    ACCESS_DENIED("ACD-403", "Access denied",  HttpStatus.FORBIDDEN),

    TOKEN_EXPIRED("TKN-401", "Token has expired", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_MISSING("ATM-401", "Authentication required", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS("BDC-401", "Bad credentials", HttpStatus.UNAUTHORIZED),

    INTERNAL_ERROR("GEN-500", "Internal Server Error",  HttpStatus.INTERNAL_SERVER_ERROR),

    GATEWAY_TIMEOUT("GWT-504", "Response time exceeded", HttpStatus.GATEWAY_TIMEOUT),
    ;

    private final String code;
    private final String title;
    private final HttpStatus status;

    ErrorCode(String code, String title, HttpStatus status) {
        this.code = code;
        this.title = title;
        this.status = status;
    }
}
