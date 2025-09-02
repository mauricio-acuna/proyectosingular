package com.aireadiness.catalog.repository;

import com.aireadiness.catalog.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    
    /**
     * Find all roles that have at least one published version
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.versions v WHERE v.isPublished = true")
    List<Role> findAllWithPublishedVersions();
    
    /**
     * Find role with its published version
     */
    @Query("SELECT r FROM Role r JOIN FETCH r.versions v WHERE r.id = :roleId AND v.isPublished = true")
    Optional<Role> findByIdWithPublishedVersion(String roleId);
}
