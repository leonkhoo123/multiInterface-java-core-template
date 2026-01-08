package com.leon.rest_api.repository;

import com.leon.rest_api.entities.NovelInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NovelInfoRepository extends JpaRepository<NovelInfo, Long> {

    public Optional<NovelInfo> findByNovelName(String novelName);
}
