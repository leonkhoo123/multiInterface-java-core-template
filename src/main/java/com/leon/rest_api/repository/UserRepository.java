package com.leon.rest_api.repository;

import com.leon.common.entities.User;
import com.leon.common.entities.UserInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserInterface> findByUsername(String username);

    Optional<UserInterface> findById(long id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
