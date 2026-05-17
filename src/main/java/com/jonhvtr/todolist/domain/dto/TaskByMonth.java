package com.jonhvtr.todolist.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TaskByMonth(
        @Min(value = 1900, message = "deve estar entre 1900 e 2099")
        @Max(value = 2099, message = "deve estar entre 1900 e 2099")
        @NotNull
        Integer year,
        @Min(value = 1, message = "deve estar entre 1 e 12")
        @Max(value = 12, message = "deve estar entre 1 e 12")
        Integer month) {
}
