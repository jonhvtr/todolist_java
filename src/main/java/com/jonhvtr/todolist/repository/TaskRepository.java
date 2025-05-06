package com.jonhvtr.todolist.repository;

import com.jonhvtr.todolist.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
