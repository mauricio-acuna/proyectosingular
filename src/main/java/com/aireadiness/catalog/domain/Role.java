package com.aireadiness.catalog.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Professional role entity (Backend Java, Customer Support, etc.)
 * Based on PRD section 13 - Database model
 */
@Entity
@Table(name = "role")
public class Role {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoleVersion> versions = new ArrayList<>();
    
    public Role() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Role(String id, String name, String description) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<RoleVersion> getVersions() { return versions; }
    public void setVersions(List<RoleVersion> versions) { this.versions = versions; }
}
