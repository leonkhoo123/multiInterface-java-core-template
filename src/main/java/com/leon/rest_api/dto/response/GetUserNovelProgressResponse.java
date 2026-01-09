package com.leon.rest_api.dto.response;

public class GetUserNovelProgressResponse {
    private Long novelId;
    private String novelName;
    private Long readUntil;
    private Long totalSeq;

    public GetUserNovelProgressResponse() {
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

    public Long getReadUntil() {
        return readUntil;
    }

    public void setReadUntil(Long readUntil) {
        this.readUntil = readUntil;
    }

    public Long getTotalSeq() {
        return totalSeq;
    }

    public void setTotalSeq(Long totalSeq) {
        this.totalSeq = totalSeq;
    }
}
