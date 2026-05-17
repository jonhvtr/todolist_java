package com.jonhvtr.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jonhvtr.todolist.domain.enums.Priority;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TaskRequest(
        @NotBlank(message = "{task.title.not_blank}") String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime dueDate,
        Priority priority,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime reminderDateTime) {
}
