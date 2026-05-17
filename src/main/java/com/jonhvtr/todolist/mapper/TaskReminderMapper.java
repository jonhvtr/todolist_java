package com.jonhvtr.todolist.mapper;

import com.jonhvtr.todolist.domain.dto.AddDate;
import com.jonhvtr.todolist.domain.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskReminderMapper {

    @Mapping(target = "reminderDateTime", source = "dateTime")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void applyReminder(AddDate dto, @MappingTarget Task task);

    @Mapping(target = "reminderDateTime", expression = "java(null)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "priority", ignore = true)
    void clearReminder(Task source, @MappingTarget Task task);
}
