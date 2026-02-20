package com.example.demo.security;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_deberiaGenerarTokenValido() {
        String token = jwtService.generateToken("testuser", List.of("USER"));

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_deberiaExtraerUsernameDelToken() {
        String token = jwtService.generateToken("testuser", List.of("USER"));

        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void extractAllClaims_deberiaExtraerClaimsDelToken() {
        List<String> roles = List.of("USER", "ADMIN");
        String token = jwtService.generateToken("testuser", roles);

        Claims claims = jwtService.extractAllClaims(token);

        assertEquals("testuser", claims.getSubject());
        assertNotNull(claims.get("roles"));
    }

    @Test
    void generateToken_conMultiplesRoles_deberiaIncluirTodosLosRoles() {
        List<String> roles = List.of("USER", "ADMIN");
        String token = jwtService.generateToken("adminuser", roles);

        Claims claims = jwtService.extractAllClaims(token);

        @SuppressWarnings("unchecked")
        List<String> extractedRoles = (List<String>) claims.get("roles");
        assertEquals(2, extractedRoles.size());
        assertTrue(extractedRoles.contains("USER"));
        assertTrue(extractedRoles.contains("ADMIN"));
    }

    @Test
    void generateToken_tokensConDiferentesUsuarios_deberianSerDiferentes() {
        String token1 = jwtService.generateToken("user1", List.of("USER"));
        String token2 = jwtService.generateToken("user2", List.of("USER"));

        assertNotEquals(token1, token2);
        assertEquals("user1", jwtService.extractUsername(token1));
        assertEquals("user2", jwtService.extractUsername(token2));
    }
}

