package com.leon.rest_api.service;

import com.leon.rest_api.dto.DecodingResult;
import com.leon.rest_api.entities.NovelContent;
import com.leon.rest_api.entities.NovelInfo;
import com.leon.rest_api.repository.NovelContentRepository;
import com.leon.rest_api.repository.NovelInfoRepository;
import com.leon.rest_api.utils.AiUtils;
import com.leon.rest_api.utils.TextFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class NovelProcessorService {
    private static final Logger logger = LoggerFactory.getLogger(NovelProcessorService.class);

    private final TextFileUtils textFileUtils;
    private final AiService aiService;
    private final NovelInfoRepository novelInfoRepository;
    private final NovelContentRepository novelContentRepository;

    public NovelProcessorService(TextFileUtils textFileUtils, AiService aiService,
                                 NovelInfoRepository novelInfoRepository,
                                 NovelContentRepository novelContentRepository) {
        this.textFileUtils = textFileUtils;
        this.aiService = aiService;
        this.novelInfoRepository = novelInfoRepository;
        this.novelContentRepository = novelContentRepository;
    }

    public void consumeNovel() {
        logger.info("start to consume novel");
        
        File folder = new File("novel_data");
        if (!folder.exists() || !folder.isDirectory()) {
            logger.error("novel_data directory not found");
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            logger.warn("No files found in novel_data");
            return;
        }

        // Order by size small to big
        Arrays.sort(files, Comparator.comparingLong(File::length));

        for (File file : files) {
            if (file.isFile()) {
                String novelPath = file.getPath();
                logger.info("Processing file: {}", novelPath);
                try {
                    DecodingResult decodingResult = TextFileUtils.decodingVerification(novelPath);
                    String novelName = aiService.inquiryLocalLlm(AiUtils.getNovelNamePrompt(novelPath));

                    if (novelName == null || novelName.isEmpty() || novelName.equals("unknown_name")) {
                        logger.error("Failed to get novel name for {}", novelPath);
                        continue;
                    }
                    // check if novel exists
                    Optional<NovelInfo> novelInfo = novelInfoRepository.findByNovelName(novelName);
                    if(novelInfo.isPresent() && novelInfo.get().isUploadCompleted()){
                        logger.info("Skipping...Novel already uploaded: {}", novelName);
                        continue;
                    }else if(novelInfo.isPresent() && !novelInfo.get().isUploadCompleted()){
                        logger.info("Novel [{}] found, but last upload broken Cleaning...", novelName);
                        novelInfoRepository.delete(novelInfo.get());
                        novelContentRepository.deleteByNovelId(novelInfo.get().getNovelId());
                    }
                    
                    processNovelFile(novelName, novelPath, decodingResult);
                } catch (Exception e) {
                    logger.error("Failed to process novel: {}", novelPath, e);
                }
            }
        }
    }

    public void processNovelFile(String novelName, String filePath, DecodingResult decodingResult) {
        logger.info("Processing novel: {}, file: {}, encoding: {}", novelName, filePath, decodingResult.encoding());

        try {
            // 1. Create NovelInfo
            long novelId = System.currentTimeMillis(); // Simple ID generation, consider a sequence or UUID in production
            NovelInfo novelInfo = new NovelInfo();
            novelInfo.setNovelId(novelId);
            novelInfo.setNovelName(novelName);
            novelInfo.setAddedTime(Timestamp.from(Instant.now()));
            novelInfo.setUploadCompleted(false);

            // We will update seqCount and characterCount after processing
            novelInfo.setSeqCount(0L);
            novelInfo.setCharacterCount(0L);

            // Save initially to ensure ID exists if needed, though we update later
            novelInfo = novelInfoRepository.save(novelInfo);

            // 2. Read file and split content
            List<NovelContent> contents = new ArrayList<>();
            long totalCharacterCount = 0;
            long seqNo = 1;

//            StringBuilder buffer = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath), Charset.forName(decodingResult.encoding())))) {
                int savingCount = 0;
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append('\n');
                    totalCharacterCount += line.length();

                    //if buffer more than 400 character
                    if(buffer.length()>=500) {
                        NovelContent content = new NovelContent();
                        content.setNovelId(novelId);
                        content.setSeqNo(seqNo++);
                        content.setContent(buffer.toString());
                        contents.add(content);
                        //reset
                        buffer.setLength(0);
                    }

                    if (contents.size()>=500){
                        savingCount++;
                        logger.info("Novel: [{}], Saving batch: [{}]", novelName,savingCount);
                        // save by batch
                        novelContentRepository.saveAll(contents);
                        contents.clear();
                    }

                }
                // if got any leftover
                if (buffer.length()>0){
                    NovelContent content = new NovelContent();
                    content.setNovelId(novelId);
                    content.setSeqNo(seqNo++);
                    content.setContent(buffer.toString());
                    contents.add(content);
                    //reset
                    buffer.setLength(0);
                    savingCount++;
                    logger.info("Novel: [{}], Saving batch: [{}]", novelName,savingCount);
                    // save by batch
                    novelContentRepository.saveAll(contents);
                    contents.clear();
                }
            }

            // 3. Update NovelInfo with stats
            // java will set seqNo first only execute seqNo++, so need to -1 here to preserve the original value
            novelInfo.setSeqCount(seqNo-1);
            novelInfo.setCharacterCount(totalCharacterCount);
            novelInfo.setUploadCompleted(true);
            novelInfoRepository.save(novelInfo);

            logger.info("Successfully processed novel: {}. Total sequences: {}, Total characters: {}",
                    novelName, seqNo-1, totalCharacterCount);

        } catch (Exception e) {
            logger.error("Failed to process novel file: {}", filePath, e);
            throw new RuntimeException("Failed to process novel file", e);
        }
    }
}
