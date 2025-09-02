package com.aireadiness.catalog.repository;

import com.aireadiness.catalog.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find all active roles
     */
    Page<Role> findByActiveTrue(Pageable pageable);
    
    /**
     * Find active role by id
     */
    Optional<Role> findByIdAndActiveTrue(Long roleId);
    
    /**
     * Find roles by category
     */
    List<Role> findByActiveTrueAndCategory(String category);
    
    /**
     * Find all roles that have at least one active version
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.versions v WHERE v.active = true AND r.active = true")
    List<Role> findAllWithActiveVersions();
    
    /**
     * Find role with its active version
     */
    @Query("SELECT r FROM Role r JOIN FETCH r.versions v WHERE r.id = :roleId AND v.active = true AND r.active = true")
    Optional<Role> findByIdWithActiveVersion(@Param("roleId") Long roleId);
}
