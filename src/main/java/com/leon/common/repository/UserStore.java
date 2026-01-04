package com.leon.common.repository;
import com.leon.common.entities.UserInterface;

import java.util.Optional;

public interface UserStore {

    Optional<UserInterface> findByUsername(String username);

    Optional<UserInterface> findById(long id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    void save(UserInterface user);
}
