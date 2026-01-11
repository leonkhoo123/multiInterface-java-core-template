package com.leon.rest_api.repository;

import com.leon.common.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findById(long id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
