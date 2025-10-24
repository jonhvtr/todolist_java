package com.jonhvtr.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jonhvtr.todolist.domain.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public record TaskRequest(
        @NotBlank(message = "O título é obrigatório") String title,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime dueDate,
        Priority priority,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime reminderDateTime) {
}
