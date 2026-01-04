package com.leon.common.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface UserInterface extends UserDetails {

    Long getId();
    void setId(Long id);

    @Override
    String getUsername();
    void setUsername(String username);

    String getEmail();
    void setEmail(String email);

    @Override
    String getPassword();
    void setPassword(String password);

    LocalDateTime getLastLogin();
    void setLastLogin(LocalDateTime lastLogin);

    Set<String> getRoles();
    void setRoles(Set<String> roles);

    @Override
    Collection<? extends GrantedAuthority> getAuthorities();

    @Override
    boolean isAccountNonExpired();

    @Override
    boolean isAccountNonLocked();

    @Override
    boolean isCredentialsNonExpired();

    @Override
    boolean isEnabled();
}

