package com.leon.rest_api.scheduler;

import com.leon.rest_api.repository.BlacklistedTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class TokenCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupTask.class);
    private final BlacklistedTokenRepository repository;

    public TokenCleanupTask(BlacklistedTokenRepository repository) {
        this.repository = repository;
    }

    // Runs every hour to delete expired rows
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanExpiredTokens() {
        log.info("Starting scheduled cleanup of expired blacklisted tokens...");
        repository.deleteByExpiryDateBefore(Instant.now());
        log.info("Tokens clean up completed");
    }
}