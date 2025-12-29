package com.leon.rest_api.dto.response;

import java.time.Instant;
import java.time.LocalDateTime;

public record LogoutResponse (String username, LocalDateTime logoutTime) {
}
