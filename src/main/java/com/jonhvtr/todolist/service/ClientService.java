package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.domain.dto.ClientAuth;
import com.jonhvtr.todolist.domain.dto.ClientRequest;
import com.jonhvtr.todolist.domain.dto.ClientResponse;
import com.jonhvtr.todolist.domain.dto.TokenJWT;
import com.jonhvtr.todolist.domain.entities.Client;
import com.jonhvtr.todolist.domain.entities.Role;
import com.jonhvtr.todolist.domain.enums.RoleName;
import com.jonhvtr.todolist.exception.client.ClientEmailAlreadyExistsException;
import com.jonhvtr.todolist.exception.client.ClientEmailInvalidException;
import com.jonhvtr.todolist.exception.task.RoleNotFoundException;
import com.jonhvtr.todolist.infra.security.TokenService;
import com.jonhvtr.todolist.mapper.ClientCreateMapper;
import com.jonhvtr.todolist.repository.ClientRepository;
import com.jonhvtr.todolist.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final ClientCreateMapper clientCreateMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public ClientService(ClientRepository clientRepository, RoleRepository roleRepository, ClientCreateMapper clientCreateMapper, PasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager, TokenService tokenService
    ) {
        this.clientRepository = clientRepository;
        this.roleRepository = roleRepository;
        this.clientCreateMapper = clientCreateMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public TokenJWT authenticateClient(ClientAuth clientAuth) {
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(clientAuth.email(), clientAuth.password());
//        Authentication authentication = authenticationManager.authenticate(authToken);

        Client client = clientRepository.findByEmail(clientAuth.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

//        if (client.isPasswordCorrect(clientAuth, passwordEncoder)) {
//            throw new BadCredentialsException("Bad credentials");
//        }

        String tokenJWT = tokenService.generateToken(
                client.getId(),
                client.getEmail(),
                client.getRoles()
        );
        return new TokenJWT(tokenJWT);
    }

    @Transactional
    public ClientResponse createUser(ClientRequest dto) {
        validateEmailUniqueness(dto.email());

        Client client = clientCreateMapper.toUserEntity(dto);
        client.setPassword(passwordEncoder.encode(dto.password()));
        Role clientRole = roleRepository.findByRoleName(RoleName.CLIENT).orElseThrow(() -> new RoleNotFoundException("Role not found"));
        client.setRoles(Set.of(clientRole));
        Client saved = clientRepository.save(client);

        log.info("Client created successfully.");
        return clientCreateMapper.toUserResponse(saved);
    }

    private void validateEmailUniqueness(String email) {
        if (clientRepository.findByEmail(email).isPresent()) {
            log.warn("Email already exists!");
            throw new ClientEmailAlreadyExistsException();
        }
    }


}
