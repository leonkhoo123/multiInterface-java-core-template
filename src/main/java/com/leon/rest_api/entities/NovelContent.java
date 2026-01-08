package com.leon.rest_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.lang.Long;
import java.util.Objects;

@Entity
@Table(name = "novel_content")
@IdClass(NovelContent_pk.class)
public class NovelContent {

    @Id
    @Column(nullable = false)
    private Long novelId;

    @Id
    @Column(nullable = false)
    private Long seqNo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public NovelContent() {
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

