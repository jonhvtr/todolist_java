package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.controller.dto.TaskRequestDTO;
import com.jonhvtr.todolist.controller.dto.TaskResponseDTO;
import com.jonhvtr.todolist.controller.dto.TaskStatusDTO;
import com.jonhvtr.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<TaskResponseDTO> allTasks() {
        return taskService.allTasks();
    }

    @PatchMapping("/{id}")
    public List<TaskResponseDTO> findByIdTask(@PathVariable Long id) {
        return taskService.findByIdTask(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createTask(@Valid @RequestBody TaskRequestDTO data) {
        taskService.createTask(data);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody TaskStatusDTO data) {
        taskService.updateStatus(id, data.status());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO data) {
        taskService.updateTask(id, data);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
