package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.domain.dto.*;
import com.jonhvtr.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("tasks")
@Validated
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(@ModelAttribute TaskFilter filter,
                                                          @PageableDefault(sort = {"title"}) Pageable pageable) {
        var allTasks = taskService.getAllTasks(filter, pageable);
        return ResponseEntity.ok(allTasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId) {
        var getTaskById = taskService.getTaskById(taskId);
        return ResponseEntity.ok(getTaskById);
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<TaskResponse>> getTasksByMonth(@Valid @ModelAttribute TaskByMonth data) {
        var tasksByMonth = taskService.getAllByMonth(data);
        return ResponseEntity.ok(tasksByMonth);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> searchTasks(@RequestParam String search) {
        return ResponseEntity.ok(taskService.searchTask(search));
    }

    @GetMapping("/reminder")
    public ResponseEntity<Page<TaskResponse>> getReminders(
            @RequestParam(required = false, defaultValue = "ALL") ReminderFilter filter,
            @PageableDefault(sort = {"title"}) Pageable pageable) {
        var tasksReminders = taskService.getReminders(filter, pageable);
        return ResponseEntity.ok(tasksReminders);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest data) {
        var newTask = taskService.createTask(data);
        URI location = URI.create("/tasks/" + newTask.id());
        return ResponseEntity.created(location).body(newTask);
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable UUID taskId) {
        taskService.completedTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/due-date")
    public ResponseEntity<Void> addDueDate(@PathVariable UUID taskId, @RequestBody AddDate date) {
        taskService.addDueDate(taskId, date);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/add-reminder")
    public ResponseEntity<Void> addReminderToTask(@PathVariable UUID taskId, @RequestBody AddDate date) {
        taskService.addReminderToTask(taskId, date);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/remove-reminder")
    public ResponseEntity<Void> removeReminderToTask(@PathVariable UUID taskId) {
        taskService.removeReminderFromTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID taskId, @RequestBody TaskUpdate data) {
        taskService.updateTask(taskId, data);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
