package com.example.spring_boot_project_api.dto.response.auditlog;

import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.AuditActionEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {
  private Long id;
  private Long userId;
  private String userEmail;
  private AuditActionEnum action;
  private String entityName;
  private Long entityId;
  private String oldValue;
  private String newValue;
  private String description;
  private String ipAddress;
  private LocalDateTime createdAt;
}