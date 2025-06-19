package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.controller.dto.TaskRequestDTO;
import com.jonhvtr.todolist.controller.dto.TaskResponseDTO;
import com.jonhvtr.todolist.entity.Task;
import com.jonhvtr.todolist.entity.enums.Status;
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
        return taskRepository.findAll().stream().map(TaskResponseDTO::new).toList();
    }

    public List<TaskResponseDTO> findByIdTask(Long id) {
        return taskRepository.findById(id).stream().map(TaskResponseDTO::new).toList();
    }

    public void createTask(TaskRequestDTO data) {
        Task taskData = new Task(data);
        taskRepository.save(taskData);
    }

    public void updateStatus(Long taskId, String statusStr) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskException::new);

        Status newStatus;
        try {
            newStatus = Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status inválido: " + statusStr);
        }

        task.changeStatus(newStatus);
        taskRepository.save(task);
    }

    public void updateTask(Long taskId, TaskRequestDTO data) {
        Task task = taskRepository.findById(taskId).orElseThrow(TaskException::new);

        String newTitle;
        String newContent;

        try {
            newTitle = data.title();
            newContent = data.content();
        } catch (TaskException e) {
            throw new TaskException();
        }

        task.changeTask(newTitle, newContent);
        taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(TaskException::new);

        try {
            taskRepository.delete(task);
        } catch (TaskException e) {
            throw new TaskException();
        }
    }
}
