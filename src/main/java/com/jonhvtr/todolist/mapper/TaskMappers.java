package com.jonhvtr.todolist.mapper;

public interface TaskMappers {
    TaskCreateMapper create();
    TaskUpdateMapper update();
    TaskDueDateMapper dueDate();
    TaskStatusMapper status();
    TaskReminderMapper reminder();
}
