package com.leon.rest_api.entities;


import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "novel_user_progress")
@IdClass(NovelUserProgress_pk.class)
public class NovelUserProgress {

    @Id
    @Column(nullable = false)
    private String username;

    @Id
    @Column(nullable = false,unique = true)
    private Long novelId;

    @Column(nullable = false)
    private Long readUntil;

    @Column(nullable = true)
    private Timestamp lastRead;

    public NovelUserProgress() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Long getReadUntil() {
        return readUntil;
    }

    public void setReadUntil(Long readUntil) {
        this.readUntil = readUntil;
    }

    public Timestamp getLastRead() {
        return lastRead;
    }

    public void setLastRead(Timestamp lastRead) {
        this.lastRead = lastRead;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NovelUserProgress that = (NovelUserProgress) o;
        return Objects.equals(username, that.username) && Objects.equals(novelId, that.novelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, novelId);
    }
}
