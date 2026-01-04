package com.leon.rest_api.repository;


import com.leon.common.entities.RefreshToken;
import com.leon.common.entities.RefreshTokenInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshTokenInterface> findByTokenHash(String tokenHash);

    @Query("select r from RefreshToken r where r.tokenHash = ?1 and r.revoked = false")
    Optional<RefreshTokenInterface> findByActiveTokenHash(String tokenHash);

    Optional<RefreshTokenInterface> findByUserId(long userId);

    @Modifying
    int deleteByUserId(long userId);

    // ensure this update never rollback
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true, r.revokedAt = CURRENT_TIMESTAMP, r.revokeReason = ?1 WHERE r.userId = ?2 AND r.revoked = false")
    void revokeAllRefreshToken(String revokeReason, long userId);
}