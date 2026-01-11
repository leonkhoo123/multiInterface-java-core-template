package com.leon.rest_api.dto.response;

public class GetUserLastReadResponse {
    private Long novelId;

    public GetUserLastReadResponse() {
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
}
