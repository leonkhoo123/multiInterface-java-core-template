package com.leon.rest_api.repository.store;

import com.leon.common.entities.User;
import com.leon.common.entities.UserInterface;
import com.leon.common.repository.UserStore;
import com.leon.rest_api.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaUserStore implements UserStore {
    private final UserRepository userRepository;

    public JpaUserStore(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserInterface> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<UserInterface> findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void save(UserInterface user) {
        userRepository.save((User) user);
    }
}
