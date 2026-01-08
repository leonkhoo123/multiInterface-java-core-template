package com.leon.rest_api.entities;

import java.util.Objects;

public class NovelContent_pk{
    private Long novelId;
    private Long seqNo;

    public NovelContent_pk() {
    }

    public NovelContent_pk(Long novelId, Long seqNo) {
        this.novelId = novelId;
        this.seqNo = seqNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovelContent_pk that = (NovelContent_pk) o;
        return Objects.equals(novelId, that.novelId) &&
                Objects.equals(seqNo, that.seqNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(novelId, seqNo);
    }

}
