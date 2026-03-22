package com.leon.rest_api.service;

import com.leon.rest_api.dto.NovelDetail;
import com.leon.rest_api.dto.request.NovelContentChunkRequest;
import com.leon.rest_api.dto.request.NovelContentRequest;
import com.leon.rest_api.dto.response.GetNovelListResponse;
import com.leon.rest_api.dto.response.NovelContentChunkResponse;
import com.leon.rest_api.dto.response.NovelContentResponse;
import com.leon.rest_api.entities.NovelContent;
import com.leon.rest_api.entities.NovelInfo;
import com.leon.rest_api.entities.NovelUserProgress;
import com.leon.rest_api.exception.InvalidChunkRequestException;
import com.leon.rest_api.exception.NovelSequencenotFoundException;
import com.leon.rest_api.repository.NovelContentRepository;
import com.leon.rest_api.repository.NovelInfoRepository;
import com.leon.rest_api.repository.NovelUserProgressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NovelService {

    private static final Logger logger = LoggerFactory.getLogger(NovelService.class);

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
        logger.info("Fetching novel content for user: {}, novelId: {}, seqId: {}", username, request.getNovelId(), request.getNextSeqId());

        NovelContentResponse response = new NovelContentResponse();
        NovelContent novelContent = novelContentRepository.findByNovelIdAndSeqNo(request.getNovelId(), request.getNextSeqId())
                .orElseThrow(() -> {
                    logger.error("Novel content not found for novelId: {}, seqId: {}", request.getNovelId(), request.getNextSeqId());
                    return new NovelSequencenotFoundException(
                        String.format("Novel id: %s, next seq id: %s, not found", request.getNovelId(), request.getNextSeqId())
                );
                });

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

        logger.info("Successfully fetched novel content for novelId: {}, seqId: {}", request.getNovelId(), request.getNextSeqId());
        return response;
    }

    @Transactional(readOnly = true)
    public NovelContentChunkResponse getNovelContentInChunk(String username, NovelContentChunkRequest request) {
        logger.info("Fetching novel content chunk for user: {}, novelId: {}, fromSeq: {}, toSeq: {}", 
                username, request.getNovelId(), request.getFromSeqId(), request.getToSeqId());

        if (request.getFromSeqId() > request.getToSeqId()) {
            logger.warn("Invalid chunk request: fromSeqId {} > toSeqId {}", request.getFromSeqId(), request.getToSeqId());
            throw new InvalidChunkRequestException("From sequence ID cannot be greater than To sequence ID");
        }
        NovelInfo info = novelInfoRepository.findByNovelId(request.getNovelId()).orElseThrow(()-> {
            logger.warn("Invalid chunk request: novelId {} not found", request.getNovelId());
            return new NovelSequencenotFoundException(
                    String.format("Novel id: %s, next seq id: %s, not found", request.getNovelId())
            );
        });
        if (request.getFromSeqId() > info.getSeqCount()) {
            logger.warn("Invalid chunk request: fromSeqId {} not found for novelId {}", request.getFromSeqId(), request.getNovelId());
            throw new InvalidChunkRequestException("From sequence ID not found");
        }

        List<NovelContent> contents = novelContentRepository.findByNovelIdAndSeqNoBetween(
                request.getNovelId(), request.getFromSeqId(), request.getToSeqId());

        NovelContentChunkResponse response = new NovelContentChunkResponse();
        response.setNovelId(request.getNovelId());

        Map<String, String> contentMap = contents.stream()
                .collect(Collectors.toMap(c -> String.valueOf(c.getSeqNo()), NovelContent::getContent));
        response.setContentMap(contentMap);

        response.setTotalSeqId(info.getSeqCount());

        logger.info("Successfully fetched novel content chunk for novelId: {}, count: {}", request.getNovelId(), contents.size());
        return response;
    }

    @Transactional(readOnly = true)
    public GetNovelListResponse getNovelList(String username) {
        logger.info("Fetching novel list for user: {}", username);
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
        
        logger.info("Successfully fetched novel list for user: {}, count: {}", username, response.getNovelInfoList().size());
        return response;
    }
}
