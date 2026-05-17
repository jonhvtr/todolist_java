package com.jonhvtr.todolist.domain.dto;

import org.springframework.http.HttpMethod;

public record RouteConfig(String path, HttpMethod... method) {
}
