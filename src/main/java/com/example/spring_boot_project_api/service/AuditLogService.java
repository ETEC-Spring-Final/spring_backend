package com.example.spring_boot_project_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.spring_boot_project_api.dto.response.auditlog.AuditLogResponseDTO;
import com.example.spring_boot_project_api.enums.AuditActionEnum;

public interface AuditLogService {

  void log(Long userId, AuditActionEnum action, String entityName, Long entityId,
      Object oldValue, Object newValue, String description);

  Page<AuditLogResponseDTO> getAllAuditLogs(String entityName, Long userId, AuditActionEnum action,
      Pageable pageable);

  Page<AuditLogResponseDTO> getAuditLogsForEntity(String entityName, Long entityId, Pageable pageable);
}