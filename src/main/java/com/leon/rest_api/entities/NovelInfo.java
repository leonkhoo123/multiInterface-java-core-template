package com.leon.rest_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.kafka.common.protocol.types.Field;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "novel_info")
public class NovelInfo {

    @Id
    @Column(nullable = false)
    private Integer novelId;

    @Column(nullable = false)
    private String novelName;

    @Column(nullable = false)
    private Integer progressSeqNo;

    @Column(nullable = false)
    private Boolean lastRead;

    public NovelInfo() {
    }

    public Integer getNovelId() {
        return novelId;
    }

    public void setNovelId(Integer novelId) {
        this.novelId = novelId;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public Integer getProgressSeqId() {
        return progressSeqNo;
    }

    public void setProgressSeqId(Integer progressSeqNo) {
        this.progressSeqNo = progressSeqNo;
    }

    public Boolean getLastRead() {
        return lastRead;
    }

    public void setLastRead(Boolean lastRead) {
        this.lastRead = lastRead;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NovelInfo novelInfo = (NovelInfo) o;
        return Objects.equals(novelId, novelInfo.novelId) && Objects.equals(novelName, novelInfo.novelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(novelId, novelName);
    }
}
