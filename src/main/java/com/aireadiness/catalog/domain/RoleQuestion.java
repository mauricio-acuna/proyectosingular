package com.aireadiness.catalog.domain;

import jakarta.persistence.*;

/**
 * Role-Question relationship entity with weight and order
 * Based on PRD section 13 - Database model
 */
@Entity
@Table(name = "role_question")
public class RoleQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_version_id", nullable = false)
    private RoleVersion roleVersion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Column(nullable = false)
    private Double weight = 1.0;
    
    @Column(name = "question_order", nullable = false)
    private Integer order;
    
    public RoleQuestion() {}
    
    public RoleQuestion(RoleVersion roleVersion, Question question, Double weight, Integer order) {
        this.roleVersion = roleVersion;
        this.question = question;
        this.weight = weight;
        this.order = order;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public RoleVersion getRoleVersion() { return roleVersion; }
    public void setRoleVersion(RoleVersion roleVersion) { this.roleVersion = roleVersion; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }
}
