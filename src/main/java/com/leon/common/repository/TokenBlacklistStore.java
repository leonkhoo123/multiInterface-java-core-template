package com.leon.common.repository;

import com.leon.common.entities.BlacklistedTokenInterface;

import java.time.Instant;
import java.util.Optional;

public interface TokenBlacklistStore {

    void save(String token, Instant expiry);

    Optional<BlacklistedTokenInterface> findToken(String token);
}

