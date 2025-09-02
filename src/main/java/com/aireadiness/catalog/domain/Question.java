package com.aireadiness.catalog.domain;

import com.aireadiness.common.domain.Pillar;
import com.aireadiness.common.domain.QuestionType;
import jakarta.persistence.*;

/**
 * Question entity
 * Based on PRD section 13 - Database model
 */
@Entity
@Table(name = "question")
public class Question {
    
    @Id
    private String id;
    
    @Column(name = "text_es", nullable = false, length = 1000)
    private String textEs;
    
    @Column(name = "text_en", nullable = false, length = 1000)
    private String textEn;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Pillar pillar;
    
    @Column(length = 500)
    private String help;
    
    public Question() {}
    
    public Question(String id, String textEs, String textEn, QuestionType type, Pillar pillar) {
        this.id = id;
        this.textEs = textEs;
        this.textEn = textEn;
        this.type = type;
        this.pillar = pillar;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTextEs() { return textEs; }
    public void setTextEs(String textEs) { this.textEs = textEs; }
    
    public String getTextEn() { return textEn; }
    public void setTextEn(String textEn) { this.textEn = textEn; }
    
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    
    public Pillar getPillar() { return pillar; }
    public void setPillar(Pillar pillar) { this.pillar = pillar; }
    
    public String getHelp() { return help; }
    public void setHelp(String help) { this.help = help; }
}
