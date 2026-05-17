package com.jonhvtr.todolist.repository;

import com.jonhvtr.todolist.domain.entities.Role;
import com.jonhvtr.todolist.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);
}
