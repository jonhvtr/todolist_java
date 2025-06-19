package com.jonhvtr.todolist.entity.enums;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed");

    private final String statusDescription;

    Status(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
