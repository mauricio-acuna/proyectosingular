package com.aireadiness.assessment.service;

import com.aireadiness.common.domain.Pillar;
import com.aireadiness.common.domain.QuestionType;
import com.aireadiness.catalog.domain.RoleQuestion;
import com.aireadiness.assessment.domain.Answer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for calculating assessment scores according to PRD section 7.3
 * Scoring Algorithm:
 * - Score de pregunta: Likert normalizado x/5; multiopción aciertos/total
 * - Score de sub-pilar: promedio ponderado de sus preguntas
 * - Score de pilar: promedio de sub-pilares 
 * - Score global: Σ (peso_pilar × score_pilar) → 0..100
 * - Brechas: pilares con score < 70 y preguntas < 3/5
 */
@Service
public class ScoringService {
    
    private static final double GAP_THRESHOLD_PILLAR = 70.0;
    private static final int GAP_THRESHOLD_QUESTION = 3;
    
    /**
     * Calculate scores for an assessment
     */
    public AssessmentScores calculateScores(List<Answer> answers, List<RoleQuestion> roleQuestions) {
        
        // Group questions by pillar
        Map<Pillar, List<RoleQuestion>> questionsByPillar = roleQuestions.stream()
                .collect(Collectors.groupingBy(rq -> rq.getQuestion().getPillar()));
        
        // Create answer map for quick lookup
        Map<String, Answer> answerMap = answers.stream()
                .collect(Collectors.toMap(Answer::getQuestionId, answer -> answer));
        
        // Calculate pillar scores
        Map<Pillar, Double> pillarScores = new HashMap<>();
        
        for (Map.Entry<Pillar, List<RoleQuestion>> entry : questionsByPillar.entrySet()) {
            Pillar pillar = entry.getKey();
            List<RoleQuestion> questions = entry.getValue();
            
            double pillarScore = calculatePillarScore(questions, answerMap);
            pillarScores.put(pillar, pillarScore);
        }
        
        // Calculate global score using pillar weights
        double globalScore = calculateGlobalScore(pillarScores);
        
        // Identify gaps
        List<String> gaps = identifyGaps(roleQuestions, answerMap, pillarScores);
        
        return new AssessmentScores(pillarScores, globalScore, gaps);
    }
    
    /**
     * Calculate score for a specific pillar
     */
    private double calculatePillarScore(List<RoleQuestion> questions, Map<String, Answer> answerMap) {
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;
        
        for (RoleQuestion roleQuestion : questions) {
            Answer answer = answerMap.get(roleQuestion.getQuestion().getId());
            if (answer != null) {
                double questionScore = calculateQuestionScore(roleQuestion, answer);
                double weight = roleQuestion.getWeight();
                
                totalWeightedScore += questionScore * weight;
                totalWeight += weight;
            }
        }
        
        return totalWeight > 0 ? (totalWeightedScore / totalWeight) * 100 : 0.0;
    }
    
    /**
     * Calculate score for individual question according to PRD section 7.3
     */
    private double calculateQuestionScore(RoleQuestion roleQuestion, Answer answer) {
        QuestionType type = roleQuestion.getQuestion().getType();
        
        switch (type) {
            case LIKERT:
                // Likert normalizado x/5
                if (answer.getValueNumeric() != null) {
                    return Math.min(answer.getValueNumeric(), 5) / 5.0;
                }
                return 0.0;
                
            case MULTIPLE:
                // For MVP, treat as binary (1 = correct, 0 = incorrect)
                // Future: implement proper multiple choice scoring
                if (answer.getValueNumeric() != null) {
                    return answer.getValueNumeric() > 0 ? 1.0 : 0.0;
                }
                return 0.0;
                
            case TEXT:
                // Text questions don't contribute to scoring directly
                return 0.0;
                
            default:
                return 0.0;
        }
    }
    
    /**
     * Calculate global score using pillar weights from PRD section 7.1
     */
    private double calculateGlobalScore(Map<Pillar, Double> pillarScores) {
        double globalScore = 0.0;
        
        for (Pillar pillar : Pillar.values()) {
            Double pillarScore = pillarScores.get(pillar);
            if (pillarScore != null) {
                globalScore += pillar.getDefaultWeight() * pillarScore;
            }
        }
        
        return globalScore;
    }
    
    /**
     * Identify gaps according to PRD: pilares con score < 70 y preguntas < 3/5
     */
    private List<String> identifyGaps(List<RoleQuestion> roleQuestions, 
                                     Map<String, Answer> answerMap, 
                                     Map<Pillar, Double> pillarScores) {
        
        return roleQuestions.stream()
                .filter(rq -> {
                    // Check if pillar has gap
                    Pillar pillar = rq.getQuestion().getPillar();
                    Double pillarScore = pillarScores.get(pillar);
                    boolean pillarHasGap = pillarScore != null && pillarScore < GAP_THRESHOLD_PILLAR;
                    
                    // Check if individual question has gap
                    Answer answer = answerMap.get(rq.getQuestion().getId());
                    boolean questionHasGap = false;
                    if (answer != null && answer.getValueNumeric() != null) {
                        questionHasGap = answer.getValueNumeric() < GAP_THRESHOLD_QUESTION;
                    }
                    
                    return pillarHasGap || questionHasGap;
                })
                .map(rq -> rq.getQuestion().getId())
                .collect(Collectors.toList());
    }
    
    /**
     * DTO for assessment scores
     */
    public static class AssessmentScores {
        private final Map<Pillar, Double> pillarScores;
        private final double globalScore;
        private final List<String> gaps;
        
        public AssessmentScores(Map<Pillar, Double> pillarScores, double globalScore, List<String> gaps) {
            this.pillarScores = pillarScores;
            this.globalScore = globalScore;
            this.gaps = gaps;
        }
        
        public Map<Pillar, Double> getPillarScores() { return pillarScores; }
        public double getGlobalScore() { return globalScore; }
        public List<String> getGaps() { return gaps; }
        
        /**
         * Convert to map format for API response
         */
        public Map<String, Double> getPillarScoresAsMap() {
            Map<String, Double> result = new HashMap<>();
            for (Map.Entry<Pillar, Double> entry : pillarScores.entrySet()) {
                result.put(entry.getKey().name(), Math.round(entry.getValue() * 100.0) / 100.0);
            }
            result.put("GLOBAL", Math.round(globalScore * 100.0) / 100.0);
            return result;
        }
    }
}
