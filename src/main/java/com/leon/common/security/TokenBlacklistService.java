package com.leon.common.security;

import com.leon.common.repository.TokenBlacklistStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenBlacklistService {

    private final TokenBlacklistStore store;

    public TokenBlacklistService(TokenBlacklistStore store) {
        this.store = store;
    }

    public void blacklist(String token, long ttlInMs) {
        Instant expiry = Instant.now().plusMillis(ttlInMs);

        if (store.findToken(token).isEmpty()) {
            store.save(token, expiry);
        }
    }

    public boolean isBlacklisted(String token) {
        return store.findToken(token).isPresent();
    }
}



