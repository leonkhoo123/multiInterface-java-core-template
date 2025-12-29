package com.leon.rest_api.repository;

import com.leon.rest_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during Login and JWT Filter
    Optional<User> findByUsername(String username);

    // Good for registration checks
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
