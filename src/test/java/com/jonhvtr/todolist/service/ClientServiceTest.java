package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.domain.dto.ClientRequest;
import com.jonhvtr.todolist.domain.dto.ClientResponse;
import com.jonhvtr.todolist.domain.entities.Client;
import com.jonhvtr.todolist.domain.entities.Role;
import com.jonhvtr.todolist.domain.enums.RoleName;
import com.jonhvtr.todolist.exception.client.ClientEmailAlreadyExistsException;
import com.jonhvtr.todolist.mapper.ClientCreateMapper;
import com.jonhvtr.todolist.repository.ClientRepository;
import com.jonhvtr.todolist.repository.RoleRepository;
import com.jonhvtr.todolist.utils.ClientUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ClientCreateMapper loginMappers;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    @Test
    void whenUserDataIsCorrectThenRegisterUser() {
        ClientRequest expectedRequest = ClientUtils.clientRequest();
        Client entity = ClientUtils.defaultClient();
        Role role = new Role();
        role.setRoleName(RoleName.CLIENT);

        when(clientRepository.findByEmail(expectedRequest.email()))
                .thenReturn(Optional.empty());

        when(loginMappers.toUserEntity(any(ClientRequest.class)))
                .thenReturn(entity);

        when(passwordEncoder.encode(anyString()))
                .thenAnswer(i -> "hashed_" + i.getArgument(0));

        when(roleRepository.findByRoleName(RoleName.CLIENT)).thenReturn(Optional.of(role));

        when(clientRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(loginMappers.toUserResponse(any(Client.class)))
                .thenAnswer(i -> {
                    Client client = i.getArgument(0);
                    return new ClientResponse(client.getId(), client.getEmail());
                });

        ClientResponse response = clientService.createUser(expectedRequest);

        assertThat(response.email(), is(equalTo(expectedRequest.email())));
        assertTrue(entity.getRoles().contains(role));

        verify(clientRepository).findByEmail(expectedRequest.email());
        verify(loginMappers).toUserEntity(expectedRequest);
        verify(passwordEncoder).encode(expectedRequest.password());
        verify(roleRepository).findByRoleName(RoleName.CLIENT);
        verify(clientRepository).save(any(Client.class));
        verify(loginMappers).toUserResponse(any(Client.class));
    }

    @Test
    void whenDataIsValidButEmailAlreadyExistsThenReturnClientException() {
        ClientRequest expectedRequest = ClientUtils.clientRequest();
        Client existingClient = ClientUtils.defaultClient();

        when(clientRepository.findByEmail(expectedRequest.email()))
                .thenReturn(Optional.of(existingClient));

        assertThrows(ClientEmailAlreadyExistsException.class, () -> clientService.createUser(expectedRequest));

        verify(clientRepository).findByEmail(expectedRequest.email());
        verify(clientRepository, never()).save(any());
    }

}
