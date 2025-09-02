package com.aireadiness.catalog.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Role version entity for managing role question versioning
 * Based on PRD section 13 - Database model
 */
@Entity
@Table(name = "role_version")
public class RoleVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;
    
    @Column(nullable = false)
    private Boolean active = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "roleVersion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoleQuestion> questions = new ArrayList<>();
    
    public RoleVersion() {
        this.createdAt = LocalDateTime.now();
        this.active = false;
    }
    
    public RoleVersion(Role role, Integer versionNumber) {
        this();
        this.role = role;
        this.versionNumber = versionNumber;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<RoleQuestion> getQuestions() { return questions; }
    public void setQuestions(List<RoleQuestion> questions) { this.questions = questions; }
}
