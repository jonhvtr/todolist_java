package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.controller.dto.TaskRequestDTO;
import com.jonhvtr.todolist.controller.dto.TaskResponseDTO;
import com.jonhvtr.todolist.entity.Task;
import com.jonhvtr.todolist.entity.enums.Status;
import com.jonhvtr.todolist.exception.TaskException;
import com.jonhvtr.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponseDTO> allTasks() {
        return taskRepository.findAll().stream().map(TaskResponseDTO::new).toList();
    }

    public void createTask(@RequestBody TaskRequestDTO data) {
        Task taskData = new Task(data);
        taskData.setTitle(data.title());
        taskData.setContent(data.content());

        try {
            taskData.setStatus(Status.valueOf(String.valueOf(data.status())));
        } catch (IllegalArgumentException e) {
            System.err.println("Status inválido recebido: " + data.status());
            taskData.setStatus(Status.PENDING);
        }

        try {
            taskRepository.save(taskData);
        } catch (TaskException e) {
            System.out.println(e.getMessage());
        }
    }
}
