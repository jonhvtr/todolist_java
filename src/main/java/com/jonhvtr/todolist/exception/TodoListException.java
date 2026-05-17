package com.jonhvtr.todolist.exception;

import com.jonhvtr.todolist.domain.enums.ErrorCode;
import lombok.Getter;

@Getter
public class TodoListException extends RuntimeException {
//    private final ErrorCode errorCode;
//
//    public TodoListException(ErrorCode errorCode) {
//        super(errorCode.getTitle());
//        this.errorCode = errorCode;
//    }

    public TodoListException(String message) {
        super(message);
    }


//    public TodoListException(ErrorCode errorCode, String detail) {
//        super(detail);
//        this.errorCode = errorCode;
//    }
}
