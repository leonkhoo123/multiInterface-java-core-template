package com.leon.rest_api.service;

import com.leon.rest_api.dto.request.NovelContentRequest;
import com.leon.rest_api.dto.response.GetNovelListResponse;
import com.leon.rest_api.dto.response.NovelContentResponse;
import com.leon.rest_api.entities.NovelContent;
import com.leon.rest_api.exception.NovelSequencenotFoundException;
import com.leon.rest_api.repository.NovelContentRepository;
import com.leon.rest_api.repository.NovelInfoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class NovelService {

    private final NovelContentRepository novelContentRepository;
    private final NovelInfoRepository novelInfoRepository;

    public NovelService(NovelContentRepository novelContentRepository, NovelInfoRepository novelInfoRepository) {
        this.novelContentRepository = novelContentRepository;
        this.novelInfoRepository = novelInfoRepository;
    }

    public NovelContentResponse getNovelContent(NovelContentRequest request){

        NovelContentResponse response = new NovelContentResponse();
        NovelContent novelContent = novelContentRepository.findByNovelIdAndSeqNo(request.getNovelId(), request.getNextSeqId())
                .orElseThrow(() -> new NovelSequencenotFoundException(
                        String.format("Novel id: %s, next seq id: %s, not found", request.getNovelId(), request.getNextSeqId())
                ));

        response.setNovelId(novelContent.getNovelId());
        response.setCurrentSeqId(novelContent.getSeqNo());
        if(novelContentRepository.countByNovelIdAndSeqNo(novelContent.getNovelId(), novelContent.getSeqNo()+1)>0){
            // no more next content
            response.setNextSeqId(novelContent.getSeqNo()+1);
            response.setLastSeq(false);
        }else{
            response.setNextSeqId(0L);
            response.setLastSeq(true);
        }
        response.setContent(novelContent.getContent());
        return response;
    }

    public GetNovelListResponse getNovelList(){
        GetNovelListResponse response = new GetNovelListResponse();
        response.setNovelInfoList(novelInfoRepository.findAll());
        return response;
    }
}
