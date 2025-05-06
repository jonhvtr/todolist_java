package com.jonhvtr.todolist.controller.dto;

import com.jonhvtr.todolist.entity.enums.Status;

public record TaskRequestDTO(String title, String content, Status status) {
}
