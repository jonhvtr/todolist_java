package com.jonhvtr.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(UUID id,
                           ClientResponse client,
                           String title,
                           String content,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime dueDate,
                           Status status,
                           Priority priority,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime reminderDateTime,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime createdAt,
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm") LocalDateTime updatedAt
) {

    public TaskResponse(Task task) {
        this(
                task.getId(),
                new ClientResponse(task.getClient().getId(), task.getClient().getEmail()),
                task.getTitle(),
                task.getContent(),
                task.getDueDate(),
                task.getStatus(),
                task.getPriority(),
                task.getReminderDateTime(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
