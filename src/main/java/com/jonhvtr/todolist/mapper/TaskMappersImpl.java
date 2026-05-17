package com.jonhvtr.todolist.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TaskMappersImpl implements TaskMappers {

    public final TaskCreateMapper createMapper;
    public final TaskUpdateMapper updateMapper;
    public final TaskStatusMapper statusMapper;
    public final TaskDueDateMapper dueDateMapper;
    public final TaskReminderMapper reminderMapper;

    @Override
    public TaskCreateMapper create() {
        return createMapper;
    }

    @Override
    public TaskUpdateMapper update() {
        return updateMapper;
    }

    @Override
    public TaskDueDateMapper dueDate() {
        return dueDateMapper;
    }

    @Override
    public TaskStatusMapper status() {
        return statusMapper;
    }

    @Override
    public TaskReminderMapper reminder() {
        return reminderMapper;
    }
}
