package com.bidesh.OJ.Gusion.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidesh.OJ.Gusion.entity.Difficulty;
import com.bidesh.OJ.Gusion.entity.Problem;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Optional<Problem> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<Problem> findByDifficulty(Difficulty difficulty, Pageable pageable);
}
