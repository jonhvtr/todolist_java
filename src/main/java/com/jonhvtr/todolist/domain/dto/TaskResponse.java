package com.jonhvtr.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;

import java.time.LocalDateTime;

public record TaskResponse(Long id,
                           String title,
                           String content,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime dueDate,
                           Status status,
                           Priority priority,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime reminderDateTime,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime createdAt,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime updatedAt) {

    public TaskResponse(Task task) {
        this(task.getId(), task.getTitle(), task.getContent(),
                task.getDueDate(),
                task.getStatus(),
                task.getPriority(),
                task.getReminderDateTime(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
