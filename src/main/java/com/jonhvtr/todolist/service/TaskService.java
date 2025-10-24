package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.domain.dto.*;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;
import com.jonhvtr.todolist.exception.PriorityInvalidException;
import com.jonhvtr.todolist.exception.StatusInvalidException;
import com.jonhvtr.todolist.exception.TaskNotFoundException;
import com.jonhvtr.todolist.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createTask(TaskRequest data) {
        Task taskData = Task.builder()
                .title(data.title())
                .content(data.content())
                .dueDate(data.dueDate() != null ? data.dueDate() : null)
                .status(Status.PENDING)
                .priority(data.priority() != null ? data.priority() : Priority.NONE)
                .reminderDateTime(data.reminderDateTime() != null ? data.reminderDateTime() : null)
                .build();

        log.info("Creating new task: {}", taskData.getTitle());
        return taskRepository.save(taskData);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(TaskResponse::new);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId) {
        var task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        return new TaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> searchTask(String search) {
        return taskRepository.searchTasks(search);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllByStatusOrPriority(Priority priority, Status status, Pageable pageable) {
        Page<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.getByPriorityAndStatus(priority, status, pageable);
        } else if (status != null) {
            tasks = taskRepository.getByStatus(status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.getByPriority(priority, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        return tasks.map(TaskResponse::new);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllByMonth(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Task> taskList = taskRepository.findAllByMonth(start, end);

        return taskList.stream().map(TaskResponse::new).toList();
    }

    @Transactional
    public void updateTask(TaskUpdate data) {
        Task task = taskRepository.findById(data.id()).orElseThrow(() -> new TaskNotFoundException(data.id()));

        Status newStatus;
        Priority newPriority;
        if (data.status() != null) {
            try {
                newStatus = Status.valueOf(data.status().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new StatusInvalidException(Status.valueOf(data.status()));
            }
        } else {
            newStatus = task.getStatus();
        }

        if (data.priority() != null) {
            try {
                newPriority = Priority.valueOf(data.priority().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new PriorityInvalidException(Priority.valueOf(data.priority().toUpperCase()));
            }
        } else {
            newPriority = task.getPriority();
        }

        Task updated = task.toBuilder()
                .title(data.title() != null ? data.title() : task.getTitle())
                .content(data.content() != null ? data.content() : task.getContent())
                .dueDate(data.dueDate() != null ? data.dueDate() : task.getDueDate())
                .status(newStatus)
                .priority(newPriority)
                .reminderDateTime(data.reminderDateTime() != null ? data.reminderDateTime() : task.getReminderDateTime())
                .build();

        log.info("Updating task: {}", data.id());
        taskRepository.save(updated);
    }

    @Transactional
    public void completedTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        Task updatedStatus = task.toBuilder()
                .status(Status.COMPLETED)
                .build();

        log.info("Completing task: {}", taskId);
        taskRepository.save(updatedStatus);
    }

    @Transactional
    public Task addDueDate(Long taskId, AddDate addDate) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        Task updatedStatus = task.toBuilder()
                .dueDate(addDate.dateTime() != null ? addDate.dateTime() : null)
                .build();

        log.info("Adding due date to task: {}", taskId);
        return taskRepository.save(updatedStatus);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        log.info("Deleting task: {}", taskId);
        taskRepository.delete(task);
    }

    @Transactional
    public Task addReminderToTask(Long taskId, AddDate addDate) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));

        Task addReminder = task.toBuilder()
                .reminderDateTime(addDate.dateTime())
                .build();

        log.info("Adding reminder to task {}: {}", taskId, addDate);
        return taskRepository.save(addReminder);
    }

    @Transactional
    public Task removeReminderFromTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));

        Task removeReminder = task.toBuilder()
                .reminderDateTime(null)
                .build();

        log.info("Removing reminder to task: {}", taskId);
        return taskRepository.save(removeReminder);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllReminders(Pageable pageable) {
        return taskRepository.findAllReminders(pageable).map(TaskResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getPendingReminders(Pageable pageable) {
        return taskRepository.findPendingReminders(pageable).map(TaskResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getRemindersToSendNow(Pageable pageable) {
        return taskRepository.findRemindersToSend(pageable, LocalDateTime.now()).map(TaskResponse::new);
    }
}
