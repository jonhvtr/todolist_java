package com.jonhvtr.todolist.repository;

import com.jonhvtr.todolist.domain.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    Optional<Client> findById(UUID id);
}
