package com.leon.rest_api.repository.store;

import com.leon.common.entities.User;
import com.leon.common.entities.UserInterface;
import com.leon.common.repository.UserDetailStore;
import com.leon.rest_api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaUserDetailStore implements UserDetailStore {

    private final UserRepository userRepository;

    public JpaUserDetailStore(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserDetails> findUsername(String username) {
        return userRepository.findByUsername(username).map(this::toUserDetails);
    }

    private UserDetails toUserDetails(UserInterface user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .build();
    }
}
