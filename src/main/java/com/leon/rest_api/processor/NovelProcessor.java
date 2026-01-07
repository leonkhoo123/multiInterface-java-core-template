package com.leon.rest_api.processor;

import com.leon.rest_api.dto.DecodingResult;
import com.leon.rest_api.service.EncodingCandidate;
import com.leon.rest_api.utils.TextFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NovelProcessor {
    private static final Logger logger = LoggerFactory.getLogger(NovelProcessor.class);

    private final TextFileUtils textFileUtils;

    public NovelProcessor(TextFileUtils textFileUtils) {
        this.textFileUtils = textFileUtils;
    }

    public void consumeNovel (){
        logger.info("start to consume novel");

        String novelPath = "novel_data/妖神記.txt";

        List<EncodingCandidate> candidateList = new ArrayList<>();

        textFileUtils.ENCODINGS_TO_TRY.forEach(encoding -> {
            EncodingCandidate candidate = new EncodingCandidate(encoding, "Chinese");
            candidateList.add(candidate);
        });
        DecodingResult result = textFileUtils.decodingVerification(novelPath,candidateList);

    }
}
