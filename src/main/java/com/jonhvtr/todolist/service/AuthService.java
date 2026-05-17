package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.domain.entities.Client;
import com.jonhvtr.todolist.domain.entities.UserDetailsImpl;
import com.jonhvtr.todolist.exception.client.ClientEmailNotFoundException;
import com.jonhvtr.todolist.repository.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private final ClientRepository clientRepository;

    public AuthService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client client =  clientRepository.findByEmail(email).orElseThrow(ClientEmailNotFoundException::new);
        return new UserDetailsImpl(client);
    }
}
