package com.jonhvtr.todolist.controller.dto;


import jakarta.validation.constraints.NotEmpty;

public record TaskRequestDTO(@NotEmpty(message = "O título é obrigatório") String title, String content) {
}
