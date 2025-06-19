package com.jonhvtr.todolist.controller.dto;


import jakarta.validation.constraints.NotBlank;

public record TaskRequestDTO(@NotBlank(message = "O título é obrigatório") String title,
                             @NotBlank(message = "o conteúdo é obrigatório") String content) {
}
