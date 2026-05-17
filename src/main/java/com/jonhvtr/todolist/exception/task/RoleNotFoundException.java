package com.jonhvtr.todolist.exception.task;

import com.jonhvtr.todolist.exception.TodoListException;
import lombok.Getter;

@Getter
public class RoleNotFoundException extends TodoListException {
    public RoleNotFoundException(String msg) {
        super(msg);
    }
}
