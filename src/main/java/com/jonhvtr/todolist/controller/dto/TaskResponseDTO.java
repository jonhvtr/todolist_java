package com.jonhvtr.todolist.controller.dto;

import com.jonhvtr.todolist.entity.Task;
import com.jonhvtr.todolist.entity.enums.Status;

import java.time.LocalDateTime;

public record TaskResponseDTO(Long id, String title, String content, Status status, LocalDateTime createdAt,
                              LocalDateTime updatedAt) {

    public TaskResponseDTO(Task task) {
        this(task.getId(), task.getTitle(), task.getContent(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
