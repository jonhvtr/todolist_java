package com.jonhvtr.todolist.entity;

import com.jonhvtr.todolist.controller.dto.TaskRequestDTO;
import com.jonhvtr.todolist.entity.enums.Status;
import com.jonhvtr.todolist.entity.enums.StatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "tasks")
@Entity(name = "tasks")
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String content;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    private Status status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Task(TaskRequestDTO data) {
        this.title = data.title();
        this.content = data.content();
        this.status = data.status();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
