package com.aireadiness.assessment;

import com.aireadiness.assessment.domain.Answer;
import com.aireadiness.assessment.domain.Assessment;
import com.aireadiness.assessment.service.ScoringService;
import com.aireadiness.catalog.domain.Question;
import com.aireadiness.catalog.domain.RoleQuestion;
import com.aireadiness.catalog.domain.RoleVersion;
import com.aireadiness.common.domain.Pillar;
import com.aireadiness.common.domain.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ScoringService according to PRD section 7.3
 */
class ScoringServiceTest {
    
    private ScoringService scoringService;
    
    @BeforeEach
    void setUp() {
        scoringService = new ScoringService();
    }
    
    @Test
    void calculateScores_AllPerfectAnswers_ShouldReturn100() {
        // Given: Perfect answers (all 5s)
        List<RoleQuestion> roleQuestions = createTestRoleQuestions();
        List<Answer> answers = createPerfectAnswers();
        
        // When
        ScoringService.AssessmentScores scores = scoringService.calculateScores(answers, roleQuestions);
        
        // Then
        assertEquals(100.0, scores.getGlobalScore(), 0.1);
        assertTrue(scores.getGaps().isEmpty());
    }
    
    @Test
    void calculateScores_AllLowAnswers_ShouldIdentifyGaps() {
        // Given: Low answers (all 1s and 2s)
        List<RoleQuestion> roleQuestions = createTestRoleQuestions();
        List<Answer> answers = createLowAnswers();
        
        // When
        ScoringService.AssessmentScores scores = scoringService.calculateScores(answers, roleQuestions);
        
        // Then
        assertTrue(scores.getGlobalScore() < 50.0);
        assertFalse(scores.getGaps().isEmpty());
    }
    
    @Test
    void calculateScores_MixedAnswers_ShouldCalculateCorrectWeights() {
        // Given: Mixed answers
        List<RoleQuestion> roleQuestions = createTestRoleQuestions();
        List<Answer> answers = createMixedAnswers();
        
        // When
        ScoringService.AssessmentScores scores = scoringService.calculateScores(answers, roleQuestions);
        
        // Then
        assertNotNull(scores.getPillarScores());
        assertTrue(scores.getGlobalScore() > 0);
        assertTrue(scores.getGlobalScore() < 100);
    }
    
    private List<RoleQuestion> createTestRoleQuestions() {
        RoleVersion roleVersion = new RoleVersion();
        
        Question techQuestion = new Question("q1", "Tech question", "Tech question", QuestionType.LIKERT, Pillar.TECH);
        Question aiQuestion = new Question("q2", "AI question", "AI question", QuestionType.LIKERT, Pillar.AI);
        Question commQuestion = new Question("q3", "Comm question", "Comm question", QuestionType.LIKERT, Pillar.COMMUNICATION);
        Question portfolioQuestion = new Question("q4", "Portfolio question", "Portfolio question", QuestionType.LIKERT, Pillar.PORTFOLIO);
        
        RoleQuestion rq1 = new RoleQuestion(roleVersion, techQuestion, 1.0, 1);
        RoleQuestion rq2 = new RoleQuestion(roleVersion, aiQuestion, 1.0, 2);
        RoleQuestion rq3 = new RoleQuestion(roleVersion, commQuestion, 1.0, 3);
        RoleQuestion rq4 = new RoleQuestion(roleVersion, portfolioQuestion, 1.0, 4);
        
        return Arrays.asList(rq1, rq2, rq3, rq4);
    }
    
    private List<Answer> createPerfectAnswers() {
        Assessment assessment = new Assessment();
        return Arrays.asList(
            new Answer(assessment, "q1", 5, null),
            new Answer(assessment, "q2", 5, null),
            new Answer(assessment, "q3", 5, null),
            new Answer(assessment, "q4", 5, null)
        );
    }
    
    private List<Answer> createLowAnswers() {
        Assessment assessment = new Assessment();
        return Arrays.asList(
            new Answer(assessment, "q1", 1, null),
            new Answer(assessment, "q2", 2, null),
            new Answer(assessment, "q3", 1, null),
            new Answer(assessment, "q4", 2, null)
        );
    }
    
    private List<Answer> createMixedAnswers() {
        Assessment assessment = new Assessment();
        return Arrays.asList(
            new Answer(assessment, "q1", 4, null),
            new Answer(assessment, "q2", 3, null),
            new Answer(assessment, "q3", 5, null),
            new Answer(assessment, "q4", 2, null)
        );
    }
}
