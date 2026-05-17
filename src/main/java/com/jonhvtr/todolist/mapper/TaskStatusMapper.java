package com.jonhvtr.todolist.mapper;

import com.jonhvtr.todolist.domain.entities.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskStatusMapper {

    @Mapping(target = "status", constant = "COMPLETED")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "reminderDateTime", ignore = true)
    void toCompleted(Task source, @MappingTarget Task target);
}
