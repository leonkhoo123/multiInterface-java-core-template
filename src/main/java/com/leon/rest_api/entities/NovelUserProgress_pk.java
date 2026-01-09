package com.leon.rest_api.entities;

import java.util.Objects;

public class NovelUserProgress_pk {
    private String username;
    private Long novelId;

    public NovelUserProgress_pk() {
    }

    public NovelUserProgress_pk(String username, Long novelId) {
        this.username = username;
        this.novelId = novelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovelUserProgress_pk that = (NovelUserProgress_pk) o;
        return username.equals(that.username) && novelId.equals(that.novelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, novelId);
    }

}
