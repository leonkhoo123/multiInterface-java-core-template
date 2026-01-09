package com.leon.rest_api.service;

import com.leon.rest_api.dto.request.UpdateUserNovelProgressRequest;
import com.leon.rest_api.dto.response.GetUserNovelProgressResponse;
import com.leon.rest_api.entities.NovelInfo;
import com.leon.rest_api.entities.NovelUserProgress;
import com.leon.rest_api.exception.NovelNotFoundException;
import com.leon.rest_api.repository.NovelInfoRepository;
import com.leon.rest_api.repository.NovelUserProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NovelUserProgressService {
    private final NovelUserProgressRepository novelUserProgressRepository;
    private final NovelInfoRepository novelInfoRepository;

    public NovelUserProgressService(NovelUserProgressRepository novelUserProgressRepository, NovelInfoRepository novelInfoRepository) {
        this.novelUserProgressRepository = novelUserProgressRepository;
        this.novelInfoRepository = novelInfoRepository;
    }

    @Transactional
    public void updateUserNovelProgress(String username, UpdateUserNovelProgressRequest request) {
        //update user reading progress
        Optional<NovelUserProgress> findResult = novelUserProgressRepository.findByUsernameAndNovelId(username, request.getNovelId());
        NovelUserProgress novelUserProgress;
        if (findResult.isPresent()) {
            novelUserProgress = findResult.get();
        } else {
            novelUserProgress = new NovelUserProgress();
            novelUserProgress.setUsername(username);
            novelUserProgress.setNovelId(request.getNovelId());
        }
        novelUserProgress.setReadUntil(request.getSeqNo());
        novelUserProgress.setLastRead(Timestamp.valueOf(LocalDateTime.now()));
        novelUserProgressRepository.save(novelUserProgress);
    }

    @Transactional(readOnly = true)
    public GetUserNovelProgressResponse getUserNovelProgress(String username, Long novelId) {
        NovelInfo novelInfo = novelInfoRepository.findByNovelId(novelId)
                .orElseThrow(() -> new NovelNotFoundException("Novel id: " + novelId.toString()));
        //update user reading progress
        GetUserNovelProgressResponse response = new GetUserNovelProgressResponse();
        Optional<NovelUserProgress> findResult = novelUserProgressRepository.findByUsernameAndNovelId(username, novelId);
        if(findResult.isPresent()){
            response.setReadUntil(findResult.get().getReadUntil());
        }else{
            //never read before, load first section
            response.setReadUntil(1L);
        }
        response.setNovelId(novelId);
        response.setNovelName(novelInfo.getNovelName());
        response.setTotalSeq(novelInfo.getSeqCount());
        return response;
    }
}
