package com.example.demo.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final ConcurrentHashMap<String, Boolean> blacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token) {
        blacklist.put(token, true);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}

