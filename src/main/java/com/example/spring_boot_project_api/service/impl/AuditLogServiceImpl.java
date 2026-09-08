package com.example.spring_boot_project_api.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring_boot_project_api.dto.response.auditlog.AuditLogResponseDTO;
import com.example.spring_boot_project_api.enums.AuditActionEnum;
import com.example.spring_boot_project_api.model.AuditLog;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.AuditLogRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.AuditLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

  private final AuditLogRepository auditLogRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  public void log(Long userId, AuditActionEnum action, String entityName, Long entityId,
      Object oldValue, Object newValue, String description) {
    try {
      User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

      AuditLog entry = AuditLog.builder()
          .user(user)
          .action(action)
          .entityName(entityName)
          .entityId(entityId)
          .oldValue(toJson(oldValue))
          .newValue(toJson(newValue))
          .description(description)
          .build();

      auditLogRepository.save(entry);
    } catch (Exception ex) {
      log.error("Failed to write audit log for entity={} entityId={} action={}",
          entityName, entityId, action, ex);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AuditLogResponseDTO> getAllAuditLogs(String entityName, Long userId, AuditActionEnum action,
      Pageable pageable) {
    String normalizedEntityName = (entityName == null || entityName.isBlank()) ? null : entityName;
    return auditLogRepository.search(normalizedEntityName, userId, action, pageable)
        .map(this::mapToDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AuditLogResponseDTO> getAuditLogsForEntity(String entityName, Long entityId, Pageable pageable) {
    return auditLogRepository.findByEntityNameIgnoreCaseAndEntityIdOrderByCreatedAtDesc(entityName, entityId, pageable)
        .map(this::mapToDTO);
  }

  private String toJson(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof String) {
      return (String) value;
    }
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      log.warn("Failed to serialize audit log value", e);
      return String.valueOf(value);
    }
  }

  private AuditLogResponseDTO mapToDTO(AuditLog entry) {
    return AuditLogResponseDTO.builder()
        .id(entry.getId())
        .userId(entry.getUser() != null ? entry.getUser().getId() : null)
        .userEmail(entry.getUser() != null ? entry.getUser().getEmail() : null)
        .action(entry.getAction())
        .entityName(entry.getEntityName())
        .entityId(entry.getEntityId())
        .oldValue(entry.getOldValue())
        .newValue(entry.getNewValue())
        .description(entry.getDescription())
        .ipAddress(entry.getIpAddress())
        .createdAt(entry.getCreatedAt())
        .build();
  }
}