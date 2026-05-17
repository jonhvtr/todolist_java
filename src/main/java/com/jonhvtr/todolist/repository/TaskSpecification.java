package com.jonhvtr.todolist.repository;

import com.jonhvtr.todolist.domain.dto.ReminderFilter;
import com.jonhvtr.todolist.domain.dto.TaskFilter;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskSpecification {
    private TaskSpecification() {
    }

    public static Specification<Task> titleContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            String like = "%" + search.toLowerCase() + "%";

            return cb.or(cb.like(cb.lower(root.get("title")), like));
        };
    }

    public static Specification<Task> reminderFilter(ReminderFilter filter) {
        return switch (filter) {
            case ALL -> hasReminder();
            case PENDING -> pendingReminder();
        };
    }

    public static Specification<Task> byFilter(TaskFilter filter) {
        return Specification.allOf(hasStatus(filter.status()))
                .and(hasPriority(filter.priority()));
    }

    public static Specification<Task> byClientId(UUID clientId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("client").get("id"), clientId);
    }

    public static Specification<Task> dueDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.between(root.get("dueDate"), start, end);
    }

    private static Specification<Task> hasReminder() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get("reminderDateTime"));
    }

    private static Specification<Task> pendingReminder() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("reminderDateTime")),
                        root.get("status").in(Status.PENDING, Status.IN_PROGRESS));
    }

    private static Specification<Task> hasStatus(Status status) {
        return ((root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status));
    }

    private static Specification<Task> hasPriority(Priority priority) {
        return ((root, query, criteriaBuilder) ->
                priority == null ? null : criteriaBuilder.equal(root.get("priority"), priority));
    }
}
