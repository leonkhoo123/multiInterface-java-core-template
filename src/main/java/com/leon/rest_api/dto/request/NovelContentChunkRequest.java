package com.leon.rest_api.dto.request;

import jakarta.validation.constraints.NotNull;

public class NovelContentChunkRequest {

    @NotNull(message = "Novel ID is empty")
    private Long novelId;
    @NotNull(message = "From sequence ID is empty")
    private Long fromSeqId;
    @NotNull(message = "To sequence ID is empty")
    private Long toSeqId;

    public NovelContentChunkRequest() {
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Long getFromSeqId() {
        return fromSeqId;
    }

    public void setFromSeqId(Long fromSeqId) {
        this.fromSeqId = fromSeqId;
    }

    public Long getToSeqId() {
        return toSeqId;
    }

    public void setToSeqId(Long toSeqId) {
        this.toSeqId = toSeqId;
    }
}
