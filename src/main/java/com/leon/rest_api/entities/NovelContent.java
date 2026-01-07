package com.leon.rest_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "novel_content")
public class NovelContent {

    @Id
    @Column(nullable = false)
    private Integer novelId;

    @Id
    @Column(nullable = false)
    private Integer seqNo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean lastLocation;

    public NovelContent() {
    }

    public Integer getNovelId() {
        return novelId;
    }

    public void setNovelId(Integer novelId) {
        this.novelId = novelId;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(boolean lastLocation) {
        this.lastLocation = lastLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NovelContent that = (NovelContent) o;
        return Objects.equals(novelId, that.novelId) && Objects.equals(seqNo, that.seqNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(novelId, seqNo);
    }
}
