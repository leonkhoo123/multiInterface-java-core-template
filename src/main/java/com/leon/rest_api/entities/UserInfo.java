package com.leon.rest_api.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "user_info", schema = "java")
public class UserInfo {

    @Id
    @Column(name = "user_id", nullable = false, precision = 19, scale = 0)
    private BigDecimal userId;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "balance", nullable = false, precision = 17, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "creation_date", nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP()")
    private LocalDateTime creationDate;

    @Column(name = "modification_date", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP()")
    private LocalDateTime modificationDate;

    // Getters and setters

    public BigDecimal getUserId() {
        return userId;
    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
}
