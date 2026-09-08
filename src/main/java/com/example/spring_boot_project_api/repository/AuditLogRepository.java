package com.example.spring_boot_project_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spring_boot_project_api.enums.AuditActionEnum;
import com.example.spring_boot_project_api.model.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

  @Query("SELECT a FROM AuditLog a WHERE " +
      "(:entityName IS NULL OR LOWER(a.entityName) LIKE LOWER(CONCAT('%', :entityName, '%'))) AND " +
      "(:userId IS NULL OR a.user.id = :userId) AND " +
      "(:action IS NULL OR a.action = :action) " +
      "ORDER BY a.createdAt DESC")
  Page<AuditLog> search(
      @Param("entityName") String entityName,
      @Param("userId") Long userId,
      @Param("action") AuditActionEnum action,
      Pageable pageable);

  Page<AuditLog> findByEntityNameIgnoreCaseAndEntityIdOrderByCreatedAtDesc(
      String entityName, Long entityId, Pageable pageable);
}