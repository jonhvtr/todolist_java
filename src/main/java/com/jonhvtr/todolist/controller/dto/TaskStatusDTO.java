package com.jonhvtr.todolist.controller.dto;

import jakarta.validation.constraints.Pattern;

public record TaskStatusDTO(@Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED") String status) {
}
