package com.jonhvtr.todolist;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TodolistApplication {

    static void main(String[] args) {
        log.info("Starting Todolist Application");
        SpringApplication.run(TodolistApplication.class, args);
        log.info("Todolist Application started");
    }
}
