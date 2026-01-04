package com.leon.common.security;

import com.leon.common.repository.UserDetailStore;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDetailStore userDetailStore;

    public CustomUserDetailsService(UserDetailStore userDetailStore) {
        this.userDetailStore = userDetailStore;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailStore.findUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
