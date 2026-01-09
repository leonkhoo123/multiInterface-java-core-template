package com.leon.rest_api.repository;

import com.leon.rest_api.entities.NovelContent;
import com.leon.rest_api.entities.NovelContent_pk;
import com.leon.rest_api.entities.NovelUserProgress_pk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface NovelContentRepository extends JpaRepository<NovelContent, NovelUserProgress_pk> {

        @Transactional
        @Modifying
        @Query("DELETE FROM NovelContent n WHERE n.novelId = ?1")
        void deleteByNovelId(Long novelId);

        Optional<NovelContent> findByNovelIdAndSeqNo(Long novelId, Long seqNo);

        Long countByNovelIdAndSeqNo(Long novelId, Long seqNo);

}
