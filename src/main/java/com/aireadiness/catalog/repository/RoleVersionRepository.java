package com.aireadiness.catalog.repository;

import com.aireadiness.catalog.domain.RoleQuestion;
import com.aireadiness.catalog.domain.RoleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RoleVersion entity
 */
@Repository
public interface RoleVersionRepository extends JpaRepository<RoleVersion, Long> {
    
    /**
     * Find active version for a role
     */
    @Query("SELECT rv FROM RoleVersion rv WHERE rv.role.id = :roleId AND rv.active = true")
    Optional<RoleVersion> findByRoleIdAndActiveTrue(@Param("roleId") Long roleId);
    
    /**
     * Find specific version for a role
     */
    @Query("SELECT rv FROM RoleVersion rv WHERE rv.role.id = :roleId AND rv.versionNumber = :versionNumber")
    Optional<RoleVersion> findByRoleIdAndVersionNumber(@Param("roleId") Long roleId, @Param("versionNumber") Integer versionNumber);
    
    /**
     * Find version with questions
     */
    @Query("SELECT rv FROM RoleVersion rv LEFT JOIN FETCH rv.questions rq LEFT JOIN FETCH rq.question WHERE rv.role.id = :roleId AND rv.versionNumber = :versionNumber")
    Optional<RoleVersion> findByRoleIdAndVersionNumberWithQuestions(@Param("roleId") Long roleId, @Param("versionNumber") Integer versionNumber);
    
    /**
     * Find version with questions by role string ID and version string
     * Support for string-based API endpoints
     */
    @Query("SELECT rv FROM RoleVersion rv LEFT JOIN FETCH rv.questions rq LEFT JOIN FETCH rq.question WHERE CAST(rv.role.id AS string) = :roleId AND rv.versionNumber = CAST(:version AS integer)")
    Optional<RoleVersion> findByRoleIdAndVersionWithQuestions(@Param("roleId") String roleId, @Param("version") String version);
    
    /**
     * Find published/active version by string role ID
     */
    @Query("SELECT rv FROM RoleVersion rv WHERE CAST(rv.role.id AS string) = :roleId AND rv.active = true")
    Optional<RoleVersion> findPublishedByRoleId(@Param("roleId") String roleId);
    
    /**
     * Find all versions for a role ordered by version number desc
     */
    List<RoleVersion> findByRoleIdOrderByVersionNumberDesc(Long roleId);
    
    /**
     * RoleQuestion management (since RoleQuestion doesn't have its own repository)
     */
    default RoleQuestion saveRoleQuestion(RoleQuestion roleQuestion) {
        // This would typically be handled by a separate RoleQuestionRepository
        // For now, this is a placeholder - we'll create a proper one if needed
        throw new UnsupportedOperationException("Use JPA cascading or create RoleQuestionRepository");
    }
}
