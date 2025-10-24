package com.jonhvtr.todolist.repository;

import com.jonhvtr.todolist.domain.dto.TaskResponse;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end")
    List<Task> findAllByMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Task t WHERE " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.content) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<TaskResponse> searchTasks(@Param("search") String search);

    @Query("SELECT t FROM Task t WHERE status = :status")
    Page<Task> getByStatus(@Param("status") Status status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE priority = :priority")
    Page<Task> getByPriority(@Param("priority") Priority priority, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE priority = :priority AND status = :status")
    Page<Task> getByPriorityAndStatus(@Param("priority") Priority priority, @Param("status") Status status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.reminderDateTime IS NOT NULL")
    Page<Task> findAllReminders(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.reminderDateTime IS NOT NULL " +
            "ORDER BY t.reminderDateTime ASC")
    Page<Task> findPendingReminders(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.reminderDateTime IS NOT NULL " +
            "AND t.reminderDateTime <= :dateTime")
    Page<Task> findRemindersToSend(Pageable pageable, @Param("dateTime") LocalDateTime dateTime);
}
