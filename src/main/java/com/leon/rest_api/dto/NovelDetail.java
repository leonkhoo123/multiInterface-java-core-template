package com.leon.rest_api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.sql.Timestamp;

public class NovelDetail {

    private Long novelId;

    private String novelName;

    private Long seqCount;

    private Long characterCount;

    private Timestamp addedTime;

    private String ownerUsername;

    private Long readUntil;

    private Timestamp lastRead;

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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
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
}
