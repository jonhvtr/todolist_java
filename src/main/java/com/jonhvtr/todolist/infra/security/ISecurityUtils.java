package com.jonhvtr.todolist.infra.security;

import java.util.UUID;

public interface ISecurityUtils {
    UUID getAuthenticationClientId();
    String getAuthenticationClientEmail();
}
