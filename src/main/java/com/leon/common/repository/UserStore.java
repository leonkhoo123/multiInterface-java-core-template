package com.leon.common.repository;

import com.leon.common.entities.User;

import java.util.Optional;

public interface UserStore {

    Optional<User> findByUsername(String username);

    Optional<User> findById(long id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    void save(User user);
}
