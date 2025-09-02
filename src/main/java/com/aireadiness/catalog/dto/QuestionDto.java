package com.aireadiness.catalog.dto;

import com.aireadiness.common.domain.Pillar;
import com.aireadiness.common.domain.QuestionType;

/**
 * DTO for Question response according to PRD section 12.1
 */
public class QuestionDto {
    private String id;
    private String text;
    private QuestionType type;
    private Pillar pillar;
    private Double weight;
    private String help;
    private Integer order;
    
    public QuestionDto() {}
    
    public QuestionDto(String id, String text, QuestionType type, Pillar pillar, Double weight, String help, Integer order) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.pillar = pillar;
        this.weight = weight;
        this.help = help;
        this.order = order;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    
    public Pillar getPillar() { return pillar; }
    public void setPillar(Pillar pillar) { this.pillar = pillar; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public String getHelp() { return help; }
    public void setHelp(String help) { this.help = help; }
    
    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }
}
