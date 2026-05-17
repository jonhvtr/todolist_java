package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.domain.dto.*;
import com.jonhvtr.todolist.domain.entities.Client;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.ErrorCode;
import com.jonhvtr.todolist.exception.task.TaskNotFoundException;
import com.jonhvtr.todolist.infra.security.SecurityUtils;
import com.jonhvtr.todolist.mapper.TaskMappers;
import com.jonhvtr.todolist.repository.TaskRepository;
import com.jonhvtr.todolist.repository.TaskSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMappers taskMappers;
    private final SecurityUtils securityUtils;

    public TaskService(TaskRepository taskRepository, TaskMappers taskMappers, SecurityUtils securityUtils) {
        this.taskRepository = taskRepository;
        this.taskMappers = taskMappers;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public TaskResponse createTask(TaskRequest data) {
        Client client = securityUtils.getAuthenticationClient();
        Task taskData = taskMappers.create().toTaskEntity(data);
        taskData.setClient(client);
        Task saved = taskRepository.save(taskData);

        log.info("Task created successfully: id={}, title={}", taskData.getId(), taskData.getTitle());
        return taskMappers.create().toTaskResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(TaskFilter filter, Pageable pageable) {
        UUID clientId = securityUtils.getAuthenticationClientId();
        Specification<Task> spec = TaskSpecification.byFilter(filter)
                .and(TaskSpecification.byClientId(clientId));

        return taskRepository.findAll(spec, pageable).map(TaskResponse::new);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID taskId) {
        UUID clientId = securityUtils.getAuthenticationClientId();
        var task = taskRepository.findByIdAndClientId(taskId, clientId).orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND, taskId));
        return new TaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> searchTask(String search) {
        UUID clientId = securityUtils.getAuthenticationClientId();

        Specification<Task> spec =
                TaskSpecification.byClientId(clientId)
                        .and(TaskSpecification.titleContains(search));

        return taskRepository.findAll(spec)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllByMonth(TaskByMonth data) {
        UUID clientId = securityUtils.getAuthenticationClientId();

        Integer year = data.year();
        Integer month = data.month();

        LocalDateTime start;
        LocalDateTime end;

        if (month == null) {
            start = LocalDate.of(year, 1, 1).atStartOfDay();
            end = LocalDate.of(year, 12, 31).atTime(23, 59, 59);
        } else {
            YearMonth yearMonth = YearMonth.of(year, month);
            start = yearMonth.atDay(1).atStartOfDay();
            end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        }

        Specification<Task> spec = TaskSpecification.byClientId(clientId)
                .and(TaskSpecification.dueDateBetween(start, end));

        return taskRepository.findAll(spec)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    @Transactional
    public void updateTask(UUID taskId, TaskUpdate dto) {
        UUID clientId = securityUtils.getAuthenticationClientId();
        Task task = taskRepository.findByIdAndClientId(taskId, clientId).orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND, taskId));
        taskMappers.update().updateTask(task, dto);

        log.info("Updating task: {}", taskId);
        taskRepository.save(task);
    }

    @Transactional
    public void completedTask(UUID taskId) {
        UUID clientId = securityUtils.getAuthenticationClientId();
        Task task = taskRepository.findByIdAndClientId(taskId, clientId).orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND, taskId));
        taskMappers.status().toCompleted(task, task);

        log.info("Completing task: {}", taskId);
        taskRepository.save(task);
    }

    @Transactional
    public void addDueDate(UUID taskId, AddDate addDate) {
        UUID clientId = securityUtils.getAuthenticationClientId();
        Task task = taskRepository.findByIdAndClientId(taskId, clientId).orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND, taskId));
        taskMappers.dueDate().applyDueDate(addDate, task);

        log.info("Adding due date to task: {}", taskId);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        UUID clientId = securityUtils.getAuthenticationClientId();
        Task task = taskRepository.findByIdAndClientId(taskId, clientId).orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND, taskId));

        log.info("Deleting task: {}", taskId);
        taskRepository.delete(task);
    }

    @Transactional
    public void addReminderToTask(UUID taskId, AddDate addDate) {
        UUID clientId = securityUtils.getAuthenticationClientId();
        Task task = taskRepository.findByIdAndClientId(taskId, clientId).orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND, taskId));
        taskMappers.reminder().applyReminder(addDate, task);

        log.info("Adding reminder to task {}: {}", taskId, addDate);
        taskRepository.save(task);
    }

    @Transactional
    public void removeReminderFromTask(UUID taskId) {
        UUID clientId = securityUtils.getAuthenticationClientId();
        Task task = taskRepository.findByIdAndClientId(taskId, clientId).orElseThrow(() -> new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND, taskId));
        taskMappers.reminder().clearReminder(task, task);

        log.info("Removing reminder to task: {}", taskId);
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getReminders(ReminderFilter filter, Pageable pageable) {
        UUID clientId = securityUtils.getAuthenticationClientId();

        Specification<Task> spec =
                TaskSpecification.byClientId(clientId)
                        .and(TaskSpecification.reminderFilter(filter));

        return taskRepository
                .findAll(spec, pageable)
                .map(TaskResponse::new);
    }
}
