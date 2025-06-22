package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.controller.dto.TaskRequestDTO;
import com.jonhvtr.todolist.controller.dto.TaskResponseDTO;
import com.jonhvtr.todolist.controller.dto.TaskStatusDTO;
import com.jonhvtr.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> allTasks() {
        var allTasks = taskService.allTasks();
        return ResponseEntity.ok().body(allTasks);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<List<TaskResponseDTO>> findByIdTask(@PathVariable Long id) {
        var getTaskById = taskService.findByIdTask(id);
        return ResponseEntity.ok().body(getTaskById);
    }

    @PostMapping()
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO data) {
        var newTask = taskService.createTask(data);
        URI location = URI.create("/tasks/" + newTask.getId());
        return ResponseEntity.created(location).body(new TaskResponseDTO(newTask));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody TaskStatusDTO data) {
        taskService.updateStatus(id, data.status());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO data) {
        var updatedTask = taskService.updateTask(id, data);
        return ResponseEntity.ok().body(new TaskResponseDTO(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
