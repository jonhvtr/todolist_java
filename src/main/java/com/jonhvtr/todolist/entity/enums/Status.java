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

    public static Status fromDescricao(String statusDescription) {
        for (Status status : Status.values()) {
            if (status.statusDescription.equalsIgnoreCase(statusDescription)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Nenhum Status encontrado com a descrição: " + statusDescription);
    }
}
