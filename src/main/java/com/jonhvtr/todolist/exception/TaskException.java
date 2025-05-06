package com.jonhvtr.todolist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class TaskException extends RuntimeException {
    public ProblemDetail toProblemDetail() {
        var pb = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        pb.setTitle("Erro ao criar a task");
        pb.setDetail("Dados Inválidos");

        return pb;
    }
}
