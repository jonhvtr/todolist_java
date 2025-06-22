package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.controller.dto.TaskRequestDTO;
import com.jonhvtr.todolist.controller.dto.TaskResponseDTO;
import com.jonhvtr.todolist.entity.Task;
import com.jonhvtr.todolist.entity.enums.Status;
import com.jonhvtr.todolist.exception.InvalidTask;
import com.jonhvtr.todolist.exception.TaskException;
import com.jonhvtr.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponseDTO> allTasks() {
        try {
            return taskRepository.findAll().stream().map(TaskResponseDTO::new).toList();
        } catch (InvalidTask e) {
            throw new InvalidTask("Nenhuma Task");
        }
    }

    public List<TaskResponseDTO> findByIdTask(Long id) {
        var getTaskById = taskRepository.findById(id).orElseThrow(() -> new TaskException("Task não encontrada"));

        return List.of(new TaskResponseDTO(getTaskById));
    }

    public Task createTask(TaskRequestDTO data) {
        if (data.title() == null || data.title().trim().isEmpty()) {
            throw new InvalidTask("O campo título é obrigatório.");
        }

        Task taskData = new Task(data);
        return taskRepository.save(taskData);
    }

    public void updateStatus(Long taskId, String statusStr) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException("Task não encontrada"));

        Status newStatus;
        try {
            newStatus = Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTask("Status inválido: " + statusStr);
        }

        task.changeStatus(newStatus);
        taskRepository.save(task);
    }

    public Task updateTask(Long taskId, TaskRequestDTO data) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskException("Task não encontrada"));

        String newTitle = data.title();
        String newContent = data.content();

        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new InvalidTask("O campo título é obrigatório.");
        }

        task.changeTask(newTitle.trim(), newContent);
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskException("Task não encontrada"));
        taskRepository.delete(task);
    }
}
