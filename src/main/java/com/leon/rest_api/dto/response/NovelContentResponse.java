package com.leon.rest_api.dto.response;

public class NovelContentResponse {
    private Long novelId;
    private Long currentSeqId;
    private Long nextSeqId;
    private String content;
    private boolean lastSeq;

    public NovelContentResponse() {
    }

    public NovelContentResponse(Long novelId, Long currentSeqId, String content) {
        this.novelId = novelId;
        this.currentSeqId = currentSeqId;
        this.content = content;
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Long getCurrentSeqId() {
        return currentSeqId;
    }

    public Long getNextSeqId() {
        return nextSeqId;
    }

    public void setNextSeqId(Long nextSeqId) {
        this.nextSeqId = nextSeqId;
    }

    public void setCurrentSeqId(Long currentSeqId) {
        this.currentSeqId = currentSeqId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLastSeq() {
        return lastSeq;
    }

    public void setLastSeq(boolean lastSeq) {
        this.lastSeq = lastSeq;
    }
}
