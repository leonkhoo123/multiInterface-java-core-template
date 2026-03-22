package com.leon.rest_api.dto.response;

import java.util.List;
import java.util.Map;

public class NovelContentChunkResponse {
    private Long novelId;
    private Map<String,String> contentMap;
    private Long totalSeqId;

    public NovelContentChunkResponse() {
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Map<String, String> getContentMap() {
        return contentMap;
    }

    public void setContentMap(Map<String, String> contentMap) {
        this.contentMap = contentMap;
    }

    public Long getTotalSeqId() {
        return totalSeqId;
    }

    public void setTotalSeqId(Long totalSeqId) {
        this.totalSeqId = totalSeqId;
    }
}