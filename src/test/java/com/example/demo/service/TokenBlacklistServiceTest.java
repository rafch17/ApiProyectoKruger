package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @InjectMocks
    TokenBlacklistService tokenBlacklistService;

    @Test
    void addToBlacklist_deberiaAgregarTokenALista() {
        String token = "test-token-123";

        tokenBlacklistService.addToBlacklist(token);

        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void isBlacklisted_deberiaRetornarFalseParaTokenNoBlacklisteado() {
        String token = "non-existent-token";

        assertFalse(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void addToBlacklist_deberiaPermitirMultiplesTokens() {
        String token1 = "token-1";
        String token2 = "token-2";

        tokenBlacklistService.addToBlacklist(token1);
        tokenBlacklistService.addToBlacklist(token2);

        assertTrue(tokenBlacklistService.isBlacklisted(token1));
        assertTrue(tokenBlacklistService.isBlacklisted(token2));
    }
}

