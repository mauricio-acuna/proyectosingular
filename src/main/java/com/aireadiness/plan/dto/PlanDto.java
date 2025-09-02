package com.aireadiness.plan.dto;

import java.util.List;

/**
 * DTO for 30/60/90 day plan according to PRD section 8.2
 */
public class PlanDto {
    
    private String summary;
    private Integer timeBudgetHoursPerWeek;
    private List<PriorityDto> priorities;
    
    public PlanDto() {}
    
    public PlanDto(String summary, Integer timeBudgetHoursPerWeek, List<PriorityDto> priorities) {
        this.summary = summary;
        this.timeBudgetHoursPerWeek = timeBudgetHoursPerWeek;
        this.priorities = priorities;
    }
    
    // Getters and setters
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public Integer getTimeBudgetHoursPerWeek() { return timeBudgetHoursPerWeek; }
    public void setTimeBudgetHoursPerWeek(Integer timeBudgetHoursPerWeek) { this.timeBudgetHoursPerWeek = timeBudgetHoursPerWeek; }
    
    public List<PriorityDto> getPriorities() { return priorities; }
    public void setPriorities(List<PriorityDto> priorities) { this.priorities = priorities; }
    
    /**
     * Priority DTO for each learning priority
     */
    public static class PriorityDto {
        private String name;
        private String why;
        private MilestonesDto milestones;
        private List<String> evidenceOfDone;
        
        public PriorityDto() {}
        
        public PriorityDto(String name, String why, MilestonesDto milestones, List<String> evidenceOfDone) {
            this.name = name;
            this.why = why;
            this.milestones = milestones;
            this.evidenceOfDone = evidenceOfDone;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getWhy() { return why; }
        public void setWhy(String why) { this.why = why; }
        
        public MilestonesDto getMilestones() { return milestones; }
        public void setMilestones(MilestonesDto milestones) { this.milestones = milestones; }
        
        public List<String> getEvidenceOfDone() { return evidenceOfDone; }
        public void setEvidenceOfDone(List<String> evidenceOfDone) { this.evidenceOfDone = evidenceOfDone; }
    }
    
    /**
     * Milestones DTO for 30/60/90 day tasks
     */
    public static class MilestonesDto {
        private List<TaskDto> d30;
        private List<TaskDto> d60;
        private List<TaskDto> d90;
        
        public MilestonesDto() {}
        
        public MilestonesDto(List<TaskDto> d30, List<TaskDto> d60, List<TaskDto> d90) {
            this.d30 = d30;
            this.d60 = d60;
            this.d90 = d90;
        }
        
        // Getters and setters
        public List<TaskDto> getD30() { return d30; }
        public void setD30(List<TaskDto> d30) { this.d30 = d30; }
        
        public List<TaskDto> getD60() { return d60; }
        public void setD60(List<TaskDto> d60) { this.d60 = d60; }
        
        public List<TaskDto> getD90() { return d90; }
        public void setD90(List<TaskDto> d90) { this.d90 = d90; }
    }
    
    /**
     * Task DTO for individual tasks with deliverables and resources
     */
    public static class TaskDto {
        private String task;
        private String deliverable;
        private String resource;
        
        public TaskDto() {}
        
        public TaskDto(String task, String deliverable, String resource) {
            this.task = task;
            this.deliverable = deliverable;
            this.resource = resource;
        }
        
        // Getters and setters
        public String getTask() { return task; }
        public void setTask(String task) { this.task = task; }
        
        public String getDeliverable() { return deliverable; }
        public void setDeliverable(String deliverable) { this.deliverable = deliverable; }
        
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
    }
}
