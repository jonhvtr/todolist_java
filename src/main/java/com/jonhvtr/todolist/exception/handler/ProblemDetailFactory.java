package com.jonhvtr.todolist.exception.handler;

import com.jonhvtr.todolist.domain.enums.ErrorCode;
import org.springframework.http.ProblemDetail;

import java.time.Instant;
import java.time.LocalDateTime;

public final class ProblemDetailFactory {
    private static final String APP_ERROR_CODE_PREFIX = "app:errorCode";
    private static final String TIMESTAMP_PROPERTY = "timestamp";

    private ProblemDetailFactory() {}

    public static ProblemDetail from(ErrorCode errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(
                errorCode.getStatus()
        );
        problemDetail.setTitle(errorCode.getTitle());
        problemDetail.setProperty(APP_ERROR_CODE_PREFIX, errorCode.getCode());
        problemDetail.setProperty(TIMESTAMP_PROPERTY, LocalDateTime.now());
        return problemDetail;
    }

    public static ProblemDetail from(ErrorCode errorCode, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(
                errorCode.getStatus()
        );
        problemDetail.setTitle(errorCode.getTitle());
        problemDetail.setDetail(detail);
        problemDetail.setProperty(APP_ERROR_CODE_PREFIX, errorCode.getCode());
        problemDetail.setProperty(TIMESTAMP_PROPERTY, LocalDateTime.now());
        return problemDetail;
    }
}
