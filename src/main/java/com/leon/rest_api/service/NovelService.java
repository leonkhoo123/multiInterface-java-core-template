package com.leon.rest_api.service;

import com.leon.rest_api.dto.NovelDetail;
import com.leon.rest_api.dto.request.NovelContentRequest;
import com.leon.rest_api.dto.response.GetNovelListResponse;
import com.leon.rest_api.dto.response.NovelContentResponse;
import com.leon.rest_api.entities.NovelContent;
import com.leon.rest_api.entities.NovelUserProgress;
import com.leon.rest_api.exception.NovelSequencenotFoundException;
import com.leon.rest_api.repository.NovelContentRepository;
import com.leon.rest_api.repository.NovelInfoRepository;
import com.leon.rest_api.repository.NovelUserProgressRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class NovelService {

    private final NovelContentRepository novelContentRepository;
    private final NovelInfoRepository novelInfoRepository;
    private final NovelUserProgressRepository novelUserProgressRepository;

    public NovelService(NovelContentRepository novelContentRepository, NovelInfoRepository novelInfoRepository,
                        NovelUserProgressRepository novelUserProgressRepository) {
        this.novelContentRepository = novelContentRepository;
        this.novelInfoRepository = novelInfoRepository;
        this.novelUserProgressRepository = novelUserProgressRepository;
    }

    @Transactional
    public NovelContentResponse getNovelContent(String username, NovelContentRequest request) {

        NovelContentResponse response = new NovelContentResponse();
        NovelContent novelContent = novelContentRepository.findByNovelIdAndSeqNo(request.getNovelId(), request.getNextSeqId())
                .orElseThrow(() -> new NovelSequencenotFoundException(
                        String.format("Novel id: %s, next seq id: %s, not found", request.getNovelId(), request.getNextSeqId())
                ));

        response.setNovelId(novelContent.getNovelId());
        response.setCurrentSeqId(novelContent.getSeqNo());
        if (novelContentRepository.countByNovelIdAndSeqNo(novelContent.getNovelId(), novelContent.getSeqNo() + 1) > 0) {
            // no more next content
            response.setNextSeqId(novelContent.getSeqNo() + 1);
            response.setLastSeq(false);
        } else {
            response.setNextSeqId(0L);
            response.setLastSeq(true);
        }
        response.setContent(novelContent.getContent());

        return response;
    }

    @Transactional(readOnly = true)
    public GetNovelListResponse getNovelList(String username) {
        Map<Long, NovelDetail> novelMap = new HashMap<>();
        novelUserProgressRepository.findByUsername(username).forEach(x -> {
            NovelDetail novel = new NovelDetail();
            novel.setNovelId(x.getNovelId());
            novel.setReadUntil(x.getReadUntil());
            novel.setLastRead(x.getLastRead());
            novelMap.put(x.getNovelId(), novel);
        });
        novelInfoRepository.findAll().forEach(x -> {
            NovelDetail novel = novelMap.get(x.getNovelId());
            if (novel == null) {
                novel = new NovelDetail();
                novel.setNovelId(x.getNovelId());
                novel.setReadUntil(0L);
                novel.setLastRead(null);
            }
            novel.setNovelName(x.getNovelName());
            novel.setSeqCount(x.getSeqCount());
            novel.setCharacterCount(x.getCharacterCount());
            novel.setAddedTime(x.getAddedTime());
            novel.setOwnerUsername(x.getOwnerUsername());
            novelMap.put(x.getNovelId(), novel);
        });

        GetNovelListResponse response = new GetNovelListResponse();

        response.setNovelInfoList(novelMap.values().stream()
                .sorted(Comparator.comparing(
                                        NovelDetail::getLastRead,
                                        Comparator.nullsLast(Comparator.reverseOrder()) // Nulls go to the bottom
                                )
                                .thenComparing(NovelDetail::getAddedTime, Comparator.reverseOrder())
                )
                .toList());
        return response;
    }
}
