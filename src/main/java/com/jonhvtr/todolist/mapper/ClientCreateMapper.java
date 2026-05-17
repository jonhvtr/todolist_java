package com.jonhvtr.todolist.mapper;

import com.jonhvtr.todolist.domain.dto.ClientRequest;
import com.jonhvtr.todolist.domain.dto.ClientResponse;
import com.jonhvtr.todolist.domain.entities.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientCreateMapper {
// role default client -> configurar
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Client toUserEntity(ClientRequest dto);

    ClientResponse toUserResponse(Client client);
}
