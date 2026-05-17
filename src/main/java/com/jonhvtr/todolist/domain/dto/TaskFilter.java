package com.jonhvtr.todolist.domain.dto;

import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;

public record TaskFilter(Status status, Priority priority) {
}
