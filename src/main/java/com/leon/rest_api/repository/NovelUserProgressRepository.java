package com.leon.rest_api.repository;


import com.leon.rest_api.entities.NovelUserProgress;
import com.leon.rest_api.entities.NovelUserProgress_pk;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NovelUserProgressRepository extends JpaRepository<NovelUserProgress, NovelUserProgress_pk> {

    Optional<NovelUserProgress> findByUsernameAndNovelId(String username, Long novelId);

    List<NovelUserProgress> findByUsername(String username);
}
