package com.jonhvtr.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank(message = "{user.name.not_blank}") @Size(min = 3, message = "{user.name.size}") String name,
        @NotBlank(message = "{user.email.not_blank}") @Email(message = "{user.email.invalid}") String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "{user.password.not_blank}")
        @Size(min = 8, message = "{user.password.size}")
        String password
) {
}
