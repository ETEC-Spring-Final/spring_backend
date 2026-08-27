package com.example.spring_boot_project_api.dto.response.maintenance_record;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.MaintenanceStatusEnum;
import com.example.spring_boot_project_api.enums.MaintenanceTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaintenanceRecordResponseDTO {
  private Long id;
  private Long vehicleId;
  private MaintenanceTypeEnum type;
  private String description;
  private LocalDateTime scheduledDate;
  private LocalDateTime completedDate;
  private BigDecimal cost;
  private MaintenanceStatusEnum status;
  private Long createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
