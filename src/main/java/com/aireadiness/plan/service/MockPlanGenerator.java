package com.aireadiness.plan.service;

import com.aireadiness.plan.dto.PlanDto;
import com.aireadiness.common.domain.Pillar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of PlanGenerator for MVP development
 * Generates realistic plans based on gaps and role without calling external AI
 * Based on PRD section 8 - Plan generation rules
 */
@Service
@ConditionalOnProperty(name = "app.plan.provider", havingValue = "mock", matchIfMissing = true)
public class MockPlanGenerator implements PlanGenerator {
    
    private static final int MAX_PRIORITIES = 5;
    
    @Override
    public PlanDto generatePlan(String role, Map<Pillar, Double> scores, List<String> gaps, Integer hoursPerWeek, String locale) {
        
        boolean isSpanish = locale != null && locale.startsWith("es");
        
        List<PlanDto.PriorityDto> priorities = new ArrayList<>();
        
        // Analyze gaps by pillar and generate priorities
        if (hasGapInPillar(scores, Pillar.TECH)) {
            priorities.add(createTechPriority(role, isSpanish));
        }
        
        if (hasGapInPillar(scores, Pillar.AI)) {
            priorities.add(createAIPriority(role, isSpanish));
        }
        
        if (hasGapInPillar(scores, Pillar.COMMUNICATION)) {
            priorities.add(createCommunicationPriority(role, isSpanish));
        }
        
        if (hasGapInPillar(scores, Pillar.PORTFOLIO)) {
            priorities.add(createPortfolioPriority(role, isSpanish));
        }
        
        // If no gaps, create a general improvement priority
        if (priorities.isEmpty()) {
            priorities.add(createGeneralImprovementPriority(role, isSpanish));
        }
        
        // Limit to max priorities
        if (priorities.size() > MAX_PRIORITIES) {
            priorities = priorities.subList(0, MAX_PRIORITIES);
        }
        
        String summary = isSpanish 
            ? String.format("Plan personalizado para %s con %d prioridades principales, optimizado para %d horas/semana", 
                           role, priorities.size(), hoursPerWeek)
            : String.format("Personalized plan for %s with %d main priorities, optimized for %d hours/week", 
                           role, priorities.size(), hoursPerWeek);
        
        return new PlanDto(summary, hoursPerWeek, priorities);
    }
    
    @Override
    public boolean validatePlan(PlanDto plan) {
        if (plan == null) return false;
        if (plan.getPriorities() == null || plan.getPriorities().isEmpty()) return false;
        if (plan.getPriorities().size() > MAX_PRIORITIES) return false;
        
        // Validate each priority has required fields
        for (PlanDto.PriorityDto priority : plan.getPriorities()) {
            if (priority.getName() == null || priority.getName().trim().isEmpty()) return false;
            if (priority.getMilestones() == null) return false;
            if (priority.getMilestones().getD30() == null || priority.getMilestones().getD30().isEmpty()) return false;
        }
        
        return true;
    }
    
    private boolean hasGapInPillar(Map<Pillar, Double> scores, Pillar pillar) {
        Double score = scores.get(pillar);
        return score != null && score < 70.0;
    }
    
    private PlanDto.PriorityDto createTechPriority(String role, boolean isSpanish) {
        if (role.contains("backend") || role.contains("java")) {
            return isSpanish ? createBackendTechPriorityES() : createBackendTechPriorityEN();
        }
        return isSpanish ? createGeneralTechPriorityES() : createGeneralTechPriorityEN();
    }
    
    private PlanDto.PriorityDto createBackendTechPriorityES() {
        List<PlanDto.TaskDto> d30 = Arrays.asList(
            new PlanDto.TaskDto(
                "Revisar y reforzar Spring Boot fundamentals", 
                "Proyecto personal con Spring Boot 3", 
                "https://spring.io/guides")
        );
        
        List<PlanDto.TaskDto> d60 = Arrays.asList(
            new PlanDto.TaskDto(
                "Implementar autenticación JWT con Spring Security", 
                "API REST con autenticación completa", 
                "https://spring.io/guides/gs/securing-web/")
        );
        
        List<PlanDto.TaskDto> d90 = Arrays.asList(
            new PlanDto.TaskDto(
                "Crear microservicio con comunicación asíncrona", 
                "Arquitectura de microservicios funcional", 
                "https://microservices.io/patterns/")
        );
        
        PlanDto.MilestonesDto milestones = new PlanDto.MilestonesDto(d30, d60, d90);
        
        return new PlanDto.PriorityDto(
            "Fortalecer habilidades técnicas backend",
            "Gaps identificados en Spring Boot, Security y arquitecturas",
            milestones,
            Arrays.asList("Repositorio GitHub actualizado", "Tests automatizados", "Documentación técnica")
        );
    }
    
    private PlanDto.PriorityDto createBackendTechPriorityEN() {
        List<PlanDto.TaskDto> d30 = Arrays.asList(
            new PlanDto.TaskDto(
                "Review and strengthen Spring Boot fundamentals", 
                "Personal project with Spring Boot 3", 
                "https://spring.io/guides")
        );
        
        List<PlanDto.TaskDto> d60 = Arrays.asList(
            new PlanDto.TaskDto(
                "Implement JWT authentication with Spring Security", 
                "REST API with complete authentication", 
                "https://spring.io/guides/gs/securing-web/")
        );
        
        List<PlanDto.TaskDto> d90 = Arrays.asList(
            new PlanDto.TaskDto(
                "Create microservice with asynchronous communication", 
                "Functional microservices architecture", 
                "https://microservices.io/patterns/")
        );
        
        PlanDto.MilestonesDto milestones = new PlanDto.MilestonesDto(d30, d60, d90);
        
        return new PlanDto.PriorityDto(
            "Strengthen backend technical skills",
            "Gaps identified in Spring Boot, Security and architectures",
            milestones,
            Arrays.asList("Updated GitHub repository", "Automated tests", "Technical documentation")
        );
    }
    
    private PlanDto.PriorityDto createAIPriority(String role, boolean isSpanish) {
        return isSpanish ? createAIPriorityES() : createAIPriorityEN();
    }
    
    private PlanDto.PriorityDto createAIPriorityES() {
        List<PlanDto.TaskDto> d30 = Arrays.asList(
            new PlanDto.TaskDto(
                "Dominar GitHub Copilot y ChatGPT para desarrollo", 
                "Workflow optimizado con IA", 
                "https://github.blog/2023-06-20-how-to-write-better-prompts-for-github-copilot/")
        );
        
        List<PlanDto.TaskDto> d60 = Arrays.asList(
            new PlanDto.TaskDto(
                "Integrar API de OpenAI en proyecto Java", 
                "Aplicación con capacidades de IA", 
                "https://platform.openai.com/docs/api-reference")
        );
        
        List<PlanDto.TaskDto> d90 = Arrays.asList(
            new PlanDto.TaskDto(
                "Implementar RAG (Retrieval Augmented Generation)", 
                "Sistema de Q&A inteligente", 
                "https://python.langchain.com/docs/use_cases/question_answering")
        );
        
        PlanDto.MilestonesDto milestones = new PlanDto.MilestonesDto(d30, d60, d90);
        
        return new PlanDto.PriorityDto(
            "Integrar IA en desarrollo diario",
            "Bajo uso de herramientas de IA para programación",
            milestones,
            Arrays.asList("Métricas de productividad mejoradas", "Proyecto con IA funcional", "Blog post sobre experiencia")
        );
    }
    
    private PlanDto.PriorityDto createAIPriorityEN() {
        List<PlanDto.TaskDto> d30 = Arrays.asList(
            new PlanDto.TaskDto(
                "Master GitHub Copilot and ChatGPT for development", 
                "Optimized AI workflow", 
                "https://github.blog/2023-06-20-how-to-write-better-prompts-for-github-copilot/")
        );
        
        List<PlanDto.TaskDto> d60 = Arrays.asList(
            new PlanDto.TaskDto(
                "Integrate OpenAI API in Java project", 
                "Application with AI capabilities", 
                "https://platform.openai.com/docs/api-reference")
        );
        
        List<PlanDto.TaskDto> d90 = Arrays.asList(
            new PlanDto.TaskDto(
                "Implement RAG (Retrieval Augmented Generation)", 
                "Intelligent Q&A system", 
                "https://python.langchain.com/docs/use_cases/question_answering")
        );
        
        PlanDto.MilestonesDto milestones = new PlanDto.MilestonesDto(d30, d60, d90);
        
        return new PlanDto.PriorityDto(
            "Integrate AI in daily development",
            "Low usage of AI tools for programming",
            milestones,
            Arrays.asList("Improved productivity metrics", "Functional AI project", "Blog post about experience")
        );
    }
    
    // Simplified methods for other priorities
    private PlanDto.PriorityDto createCommunicationPriority(String role, boolean isSpanish) {
        return new PlanDto.PriorityDto(
            isSpanish ? "Mejorar comunicación técnica" : "Improve technical communication",
            isSpanish ? "Brechas en documentación y presentaciones" : "Gaps in documentation and presentations",
            new PlanDto.MilestonesDto(Arrays.asList(), Arrays.asList(), Arrays.asList()),
            Arrays.asList()
        );
    }
    
    private PlanDto.PriorityDto createPortfolioPriority(String role, boolean isSpanish) {
        return new PlanDto.PriorityDto(
            isSpanish ? "Fortalecer portafolio profesional" : "Strengthen professional portfolio",
            isSpanish ? "Portfolio y proyectos necesitan mejoras" : "Portfolio and projects need improvements",
            new PlanDto.MilestonesDto(Arrays.asList(), Arrays.asList(), Arrays.asList()),
            Arrays.asList()
        );
    }
    
    private PlanDto.PriorityDto createGeneralTechPriorityES() {
        return new PlanDto.PriorityDto(
            "Actualizar habilidades técnicas",
            "Brechas técnicas generales identificadas",
            new PlanDto.MilestonesDto(Arrays.asList(), Arrays.asList(), Arrays.asList()),
            Arrays.asList()
        );
    }
    
    private PlanDto.PriorityDto createGeneralTechPriorityEN() {
        return new PlanDto.PriorityDto(
            "Update technical skills",
            "General technical gaps identified",
            new PlanDto.MilestonesDto(Arrays.asList(), Arrays.asList(), Arrays.asList()),
            Arrays.asList()
        );
    }
    
    private PlanDto.PriorityDto createGeneralImprovementPriority(String role, boolean isSpanish) {
        return new PlanDto.PriorityDto(
            isSpanish ? "Optimización continua" : "Continuous optimization",
            isSpanish ? "Plan de mejora general para mantener competitividad" : "General improvement plan to maintain competitiveness",
            new PlanDto.MilestonesDto(Arrays.asList(), Arrays.asList(), Arrays.asList()),
            Arrays.asList()
        );
    }
}
