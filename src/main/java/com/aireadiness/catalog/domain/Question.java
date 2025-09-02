package com.aireadiness.catalog.domain;

import com.aireadiness.common.domain.Pillar;
import com.aireadiness.common.domain.QuestionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Question entity
 * Based on PRD section 13 - Database model
 */
@Entity
@Table(name = "question")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 1000)
    private String text;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Pillar pillar;
    
    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    private List<String> options;
    
    @Column(length = 500)
    private String context;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Question() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }
    
    public Question(String text, QuestionType type, Pillar pillar) {
        this();
        this.text = text;
        this.type = type;
        this.pillar = pillar;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    
    public Pillar getPillar() { return pillar; }
    public void setPillar(Pillar pillar) { this.pillar = pillar; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public Boolean isActive() { return active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
