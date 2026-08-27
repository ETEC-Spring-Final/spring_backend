package com.example.spring_boot_project_api.dto.request.maintenance_record;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.MaintenanceStatusEnum;
import com.example.spring_boot_project_api.enums.MaintenanceTypeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaintenanceRecordRequestDTO {
  @NotNull
  private Long vehicleId;

  @NotNull
  private MaintenanceTypeEnum type;

  @NotBlank
  @Size(max = 255)
  private String description;

  @NotNull
  private LocalDateTime scheduledDate;

  private LocalDateTime completedDate;

  @NotNull
  private BigDecimal cost;

  @NotNull
  private MaintenanceStatusEnum status;
}
