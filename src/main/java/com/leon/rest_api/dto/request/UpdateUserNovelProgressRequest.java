package com.leon.rest_api.dto.request;

public class UpdateUserNovelProgressRequest {
    private Long novelId;
    private Long seqNo;

    public UpdateUserNovelProgressRequest() {
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
}
