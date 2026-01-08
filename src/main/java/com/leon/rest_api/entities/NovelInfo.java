package com.leon.rest_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "novel_info")
public class NovelInfo {

    @Id
    @Column(nullable = false,unique = true)
    private Long novelId;

    @Column(nullable = false,unique = true)
    private String novelName;

    @Column(nullable = false)
    private Long seqCount;

    @Column(nullable = false)
    private Long characterCount;

    @Column(nullable = false)
    private Timestamp addedTime;

    @Column(nullable = false)
    private boolean uploadCompleted;

    public NovelInfo() {
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public Long getSeqCount() {
        return seqCount;
    }

    public void setSeqCount(Long seqCount) {
        this.seqCount = seqCount;
    }

    public Long getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(Long characterCount) {
        this.characterCount = characterCount;
    }

    public Timestamp getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Timestamp addedTime) {
        this.addedTime = addedTime;
    }

    public boolean isUploadCompleted() {
        return uploadCompleted;
    }

    public void setUploadCompleted(boolean uploadCompleted) {
        this.uploadCompleted = uploadCompleted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NovelInfo novelInfo = (NovelInfo) o;
        return Objects.equals(novelId, novelInfo.novelId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(novelId);
    }
}
