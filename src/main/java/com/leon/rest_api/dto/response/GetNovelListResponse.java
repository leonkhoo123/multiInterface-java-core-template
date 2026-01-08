package com.leon.rest_api.dto.response;

import com.leon.rest_api.entities.NovelInfo;

import java.util.List;

public class GetNovelListResponse {
    private List<NovelInfo> novelInfoList;

    public GetNovelListResponse() {
    }

    public List<NovelInfo> getNovelInfoList() {
        return novelInfoList;
    }

    public void setNovelInfoList(List<NovelInfo> novelInfoList) {
        this.novelInfoList = novelInfoList;
    }
}
