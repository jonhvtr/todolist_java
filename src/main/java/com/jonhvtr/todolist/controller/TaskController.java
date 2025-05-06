package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.controller.dto.TaskRequestDTO;
import com.jonhvtr.todolist.controller.dto.TaskResponseDTO;
import com.jonhvtr.todolist.repository.TaskRepository;
import com.jonhvtr.todolist.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController extends TaskService {
    public TaskController(TaskRepository taskRepository) {
        super(taskRepository);
    }

    @GetMapping
    @Override
    public List<TaskResponseDTO> allTasks() {
        return super.allTasks();
    }

    @PostMapping
    @Override
    public void createTask(TaskRequestDTO data) {
        super.createTask(data);
    }

}
