package com.aireadiness.catalog.dto;

/**
 * DTO for Role response according to PRD section 12.1
 */
public class RoleDto {
    private String id;
    private String name;
    private String version;
    private String description;
    
    public RoleDto() {}
    
    public RoleDto(String id, String name, String version, String description) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.description = description;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
