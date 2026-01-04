package com.leon.common.repository;

import com.leon.common.entities.RefreshTokenInterface;
import com.leon.common.entities.UserInterface;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenStore {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeExpiredToken(RefreshTokenInterface refreshToken);

    public void saveRefreshToken(UserInterface user, String refreshToken);

    public void logoutUser(RefreshTokenInterface refreshToken);
    public void revokeAllRefreshToken(String message, long id);

    public Optional<RefreshTokenInterface> findByTokenHash(String hashedToken);

    public void save(RefreshTokenInterface refreshToken);
}

