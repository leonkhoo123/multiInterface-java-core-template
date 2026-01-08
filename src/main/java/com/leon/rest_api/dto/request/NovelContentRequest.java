package com.leon.rest_api.dto.request;

import jakarta.validation.constraints.NotNull;

public class NovelContentRequest {

    @NotNull(message = "Novel ID is empty")
    private Long novelId;
    @NotNull(message = "Next sequence ID is empty")
    private Long nextSeqId;

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Long getNextSeqId() {
        return nextSeqId;
    }

    public void setNextSeqId(Long nextSeqId) {
        this.nextSeqId = nextSeqId;
    }
}
