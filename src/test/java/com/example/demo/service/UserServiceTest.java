package com.example.demo.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Role;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void register_creaUsuarioConPasswordEncriptado() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("diego");
        request.setPassword("123456");

        when(userRepository.findByUsername("diego"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded");

        userService.register(request);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("diego") &&
                user.getPassword().equals("encoded")
        ));
    }

    @Test
    void registerAdmin_creaUsuarioConRolAdmin() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("adminUser");
        request.setPassword("admin123");

        when(userRepository.findByUsername("adminUser"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123"))
                .thenReturn("encodedAdmin");

        userService.registerAdmin(request);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("adminUser") &&
                user.getPassword().equals("encodedAdmin") &&
                user.getRole().equals(Role.ADMIN)
        ));
    }

    @Test
    void register_lanzaExcepcionSiUsuarioExiste() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setPassword("password");

        when(userRepository.findByUsername("existingUser"))
                .thenReturn(Optional.of(new com.example.demo.model.User()));

        try {
            userService.register(request);
        } catch (IllegalArgumentException e) {
            assertEquals("Username already exists", e.getMessage());
        }
    }

    @Test
    void registerAdmin_lanzaExcepcionSiUsuarioExiste() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setPassword("password");

        when(userRepository.findByUsername("existingUser"))
                .thenReturn(Optional.of(new com.example.demo.model.User()));

        try {
            userService.registerAdmin(request);
        } catch (IllegalArgumentException e) {
            assertEquals("Username already exists", e.getMessage());
        }
    }
}
