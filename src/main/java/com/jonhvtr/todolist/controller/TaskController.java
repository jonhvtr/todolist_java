package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.domain.dto.*;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;
import com.jonhvtr.todolist.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("tasks")
@Validated
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(@PageableDefault(size = 10, sort = {"title"}) Pageable pageable) {
        var allTasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok().body(allTasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long taskId) {
        var getTaskById = taskService.getTaskById(taskId);
        return ResponseEntity.ok().body(getTaskById);
    }


    @GetMapping("/status-priority")
    public ResponseEntity<Page<TaskResponse>> getAllByStatusOrPriority(@RequestParam(required = false) Status status,
                                                                       @RequestParam(required = false) Priority priority,
                                                                       Pageable pageable) {
        var getTask = taskService.getAllByStatusOrPriority(priority, status, pageable);
        return ResponseEntity.ok().body(getTask);
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<TaskResponse>> getTasksByMonth(@RequestParam
                                                              @Min(value = 1, message = "deve estar entre 1 e 12")
                                                              @Max(value = 12, message = "deve estar entre 1 e 12")
                                                              int month,
                                                              @RequestParam
                                                              @Min(value = 1900, message = "deve estar entre 1900 e 2099")
                                                              @Max(value = 2099, message = "deve estar entre 1900 e 2099")
                                                              int year) {
        var tasksByMonth = taskService.getAllByMonth(month, year);
        return ResponseEntity.ok().body(tasksByMonth);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> searchTasks(@RequestParam String search) {
        return ResponseEntity.ok(taskService.searchTask(search));
    }

    @GetMapping("/reminder")
    public ResponseEntity<Page<TaskResponse>> getAllReminders(@PageableDefault(size = 10, sort = {"title"}) Pageable pageable) {
        var tasksReminders = taskService.getAllReminders(pageable);
        return ResponseEntity.ok().body(tasksReminders);
    }

    @GetMapping("/reminder/pending")
    public ResponseEntity<Page<TaskResponse>> getPendingReminders(@PageableDefault(size = 10, sort = {"title"}) Pageable pageable) {
        var tasksPendingReminders = taskService.getPendingReminders(pageable);
        return ResponseEntity.ok().body(tasksPendingReminders);
    }

    @GetMapping("/reminder/to-send-now")
    public ResponseEntity<Page<TaskResponse>> getRemindersToSendNow(@PageableDefault(size = 10, sort = {"title"}) Pageable pageable) {
        var tasksPendingReminders = taskService.getRemindersToSendNow(pageable);
        return ResponseEntity.ok().body(tasksPendingReminders);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest data) {
        var newTask = taskService.createTask(data);
        URI location = URI.create("/tasks/" + newTask.getId());
        return ResponseEntity.created(location).body(new TaskResponse(newTask));
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long taskId) {
        taskService.completedTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/due-date")
    public ResponseEntity<TaskResponse> addDueDate(@PathVariable Long taskId, @RequestBody AddDate date) {
        var taskReminder = taskService.addDueDate(taskId, date);
        return ResponseEntity.ok().body(new TaskResponse(taskReminder));
    }

    @PatchMapping("/{taskId}/add-reminder")
    public ResponseEntity<TaskResponse> addReminderToTask(@PathVariable Long taskId, @RequestBody AddDate date) {
        var taskReminder = taskService.addReminderToTask(taskId, date);
        return ResponseEntity.ok().body(new TaskResponse(taskReminder));
    }

    @PatchMapping("/{taskId}/remove-reminder")
    public ResponseEntity<TaskResponse> removeReminderToTask(@PathVariable Long taskId) {
        var taskReminder = taskService.removeReminderFromTask(taskId);
        return ResponseEntity.ok().body(new TaskResponse(taskReminder));
    }

    @PutMapping
    public ResponseEntity<TaskResponse> updateTask(@RequestBody TaskUpdate data) {
        taskService.updateTask(data);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
