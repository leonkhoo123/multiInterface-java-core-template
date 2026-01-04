package com.leon.common.repository;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserDetailStore {

    Optional<UserDetails> findUsername (String username);

}
