package com.aireadiness.catalog.repository;

import com.aireadiness.catalog.domain.RoleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for RoleVersion entity
 */
@Repository
public interface RoleVersionRepository extends JpaRepository<RoleVersion, Long> {
    
    /**
     * Find published version for a role
     */
    @Query("SELECT rv FROM RoleVersion rv WHERE rv.role.id = :roleId AND rv.isPublished = true")
    Optional<RoleVersion> findPublishedByRoleId(@Param("roleId") String roleId);
    
    /**
     * Find specific version for a role
     */
    @Query("SELECT rv FROM RoleVersion rv WHERE rv.role.id = :roleId AND rv.version = :version")
    Optional<RoleVersion> findByRoleIdAndVersion(@Param("roleId") String roleId, @Param("version") String version);
    
    /**
     * Find version with questions
     */
    @Query("SELECT rv FROM RoleVersion rv LEFT JOIN FETCH rv.questions rq LEFT JOIN FETCH rq.question WHERE rv.role.id = :roleId AND rv.version = :version")
    Optional<RoleVersion> findByRoleIdAndVersionWithQuestions(@Param("roleId") String roleId, @Param("version") String version);
}
