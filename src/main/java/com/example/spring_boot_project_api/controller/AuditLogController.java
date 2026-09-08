package com.example.spring_boot_project_api.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.response.auditlog.AuditLogResponseDTO;
import com.example.spring_boot_project_api.enums.AuditActionEnum;
import com.example.spring_boot_project_api.service.AuditLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

  private final AuditLogService auditLogService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public Page<AuditLogResponseDTO> getAllAuditLogs(
      @RequestParam(required = false) String entityName,
      @RequestParam(required = false) Long userId,
      @RequestParam(required = false) AuditActionEnum action,
      @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    return auditLogService.getAllAuditLogs(entityName, userId, action, pageable);
  }

  @GetMapping("/{entityName}/{entityId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public Page<AuditLogResponseDTO> getAuditLogsForEntity(
      @PathVariable String entityName,
      @PathVariable Long entityId,
      @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    return auditLogService.getAuditLogsForEntity(entityName, entityId, pageable);
  }
}