package com.jonhvtr.todolist.mapper;

import com.jonhvtr.todolist.domain.dto.AddDate;
import com.jonhvtr.todolist.domain.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskDueDateMapper {

    @Mapping(target = "dueDate", source = "dateTime")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "reminderDateTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void applyDueDate(AddDate dto, @MappingTarget Task task);
}
