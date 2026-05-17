package com.jonhvtr.todolist.utils;

import com.jonhvtr.todolist.domain.dto.ClientResponse;
import com.jonhvtr.todolist.domain.dto.TaskRequest;
import com.jonhvtr.todolist.domain.dto.TaskResponse;
import com.jonhvtr.todolist.domain.dto.TaskUpdate;
import com.jonhvtr.todolist.domain.entities.Client;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public class TaskUtils {
    private static final UUID CLIENT_ID = UUID.randomUUID();

    public static Client defaultClient() {
        return Client.builder()
                .id(CLIENT_ID)
                .email("test@gmail.com")
                .build();
    }

    public static TaskRequest defaultTask() {
        return new TaskRequest(
                "Task",
                "Content task",
                LocalDateTime.of(2025, 12, 1, 10, 0),
                Priority.NONE,
                null
        );
    }

    public static Task defaultTaskEntityWithId(UUID id) {
        return Task.builder()
                .id(id)
                .client(defaultClient())
                .title("Task")
                .content("Content task")
                .dueDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .status(Status.PENDING)
                .priority(Priority.NONE)
                .reminderDateTime(null)
                .createdAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .build();
    }

    public static Task defaultTaskEntityWithClient(Client client) {
        UUID id = UUID.randomUUID();

        return Task.builder()
                .id(id)
                .client(client)
                .title("Task")
                .content("Content task")
                .dueDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .status(Status.PENDING)
                .priority(Priority.NONE)
                .reminderDateTime(null)
                .createdAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .build();
    }

    public static Task defaultTaskEntityWithOutReminder() {
        return Task.builder()
                .id(CLIENT_ID)
                .client(defaultClient())
                .title("Task")
                .content("Content task")
                .dueDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .status(Status.PENDING)
                .priority(Priority.NONE)
                .reminderDateTime(null)
                .createdAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .build();
    }

    public static Task defaultTaskEntityWithReminder() {
        return Task.builder()
                .id(CLIENT_ID)
                .client(defaultClient())
                .title("Task")
                .content("Content task")
                .dueDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .status(Status.PENDING)
                .priority(Priority.NONE)
                .reminderDateTime(LocalDateTime.of(2025, 11, 30, 10, 0))
                .createdAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 11, 10, 10, 0))
                .build();
    }

    public static TaskUpdate updateTask() {
        return new TaskUpdate(
                "Task 1",
                "Content task 1",
                LocalDateTime.of(2025, 11, 30, 10, 0),
                Status.IN_PROGRESS,
                Priority.NONE,
                null
        );
    }

    public static TaskResponse defaultTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                new ClientResponse(task.getClient().getId(), task.getClient().getEmail()),
                task.getTitle(),
                task.getContent(),
                task.getDueDate(),
                task.getStatus(),
                task.getPriority(),
                task.getReminderDateTime(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
