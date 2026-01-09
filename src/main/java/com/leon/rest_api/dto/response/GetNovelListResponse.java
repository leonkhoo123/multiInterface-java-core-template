package com.leon.rest_api.dto.response;

import com.leon.rest_api.dto.NovelDetail;
import com.leon.rest_api.entities.NovelInfo;

import java.util.List;

public class GetNovelListResponse {
    private List<NovelDetail> novelInfoList;

    public GetNovelListResponse() {
    }

    public List<NovelDetail> getNovelInfoList() {
        return novelInfoList;
    }

    public void setNovelInfoList(List<NovelDetail> novelInfoList) {
        this.novelInfoList = novelInfoList;
    }

    public void add(NovelDetail novelDetail){
        this.novelInfoList.add(novelDetail);
    }
}
