package com.leon.rest_api.security;

import com.leon.rest_api.entities.BlacklistedToken;
import com.leon.rest_api.repository.BlacklistedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenBlacklistService {

    private final BlacklistedTokenRepository repository;

    public TokenBlacklistService(BlacklistedTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void blacklist(String token, long ttlInMs) {
        // Calculate when the token would have naturally expired
        Instant expiry = Instant.now().plusMillis(ttlInMs);

        // Save to DB if not already there
        if (repository.findByToken(token).isEmpty()) {
            repository.save(new BlacklistedToken(token, expiry));
        }
    }

    public boolean isBlacklisted(String token) {
        return repository.findByToken(token).isPresent();
    }
}
