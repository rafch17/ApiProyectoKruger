package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
