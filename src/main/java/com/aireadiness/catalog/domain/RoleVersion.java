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
    
    @Column(nullable = false)
    private String version;
    
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "roleVersion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoleQuestion> questions = new ArrayList<>();
    
    public RoleVersion() {
        this.createdAt = LocalDateTime.now();
    }
    
    public RoleVersion(Role role, String version) {
        this();
        this.role = role;
        this.version = version;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<RoleQuestion> getQuestions() { return questions; }
    public void setQuestions(List<RoleQuestion> questions) { this.questions = questions; }
}
