package com.aireadiness.catalog.repository;

import com.aireadiness.catalog.domain.Question;
import com.aireadiness.common.domain.Pillar;
import com.aireadiness.common.domain.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Question entity
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * Find all active questions
     */
    Page<Question> findByActiveTrue(Pageable pageable);
    
    /**
     * Find active question by id
     */
    Optional<Question> findByIdAndActiveTrue(Long questionId);
    
    /**
     * Find questions by pillar
     */
    List<Question> findByActiveTrueAndPillar(Pillar pillar);
    
    /**
     * Find questions by type
     */
    List<Question> findByActiveTrueAndType(QuestionType type);
    
    /**
     * Find questions by pillar and type
     */
    List<Question> findByActiveTrueAndPillarAndType(Pillar pillar, QuestionType type);
}
