package com.jonhvtr.todolist.mapper;

import com.jonhvtr.todolist.domain.dto.TaskRequest;
import com.jonhvtr.todolist.domain.dto.TaskResponse;
import com.jonhvtr.todolist.domain.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskCreateMapper {
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "status", expression = "java(Status.PENDING)")
    @Mapping(target = "priority", expression = "java(dto.priority() != null ? dto.priority() : Priority.NONE)")
    @Mapping(target = "dueDate", expression = "java(dto.dueDate())")
    @Mapping(target = "reminderDateTime", expression = "java(dto.reminderDateTime())")
    Task toTaskEntity(TaskRequest dto);

    @Mapping(source = "client", target = "client")
    TaskResponse toTaskResponse(Task task);
}
