package com.leon.rest_api.repository.store;

import com.leon.common.entities.BlacklistedTokenInterface;
import com.leon.common.repository.TokenBlacklistStore;
import com.leon.rest_api.entities.BlacklistedToken;
import com.leon.rest_api.repository.BlacklistedTokenRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class JpaTokenBlacklistStore implements TokenBlacklistStore {

    private final BlacklistedTokenRepository repository;

    public JpaTokenBlacklistStore(BlacklistedTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(String token, Instant expiry) {
        repository.save(new BlacklistedToken(token, expiry));
    }

    @Override
    public Optional<BlacklistedTokenInterface> findToken(String token) {
        return repository.findByToken(token);
    }
}

