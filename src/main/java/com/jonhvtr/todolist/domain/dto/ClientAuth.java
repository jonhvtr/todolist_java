package com.jonhvtr.todolist.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientAuth(
        @NotBlank(message = "{user.email.invalid}") String email,
        @NotBlank(message = "{user.password.invalid}") String password) {
}
