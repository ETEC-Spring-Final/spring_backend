package com.example.spring_boot_project_api.dto.response.inspection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.spring_boot_project_api.enums.FuelLevelEnum;
import com.example.spring_boot_project_api.enums.InspectionTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InspectionResponseDTO {
  private Long id;
  private Long rentalId;
  private InspectionTypeEnum type;
  private FuelLevelEnum fuelLevel;
  private BigDecimal odometer;
  private String conditionNotes;
  private String damageReport;
  private List<String> photos;

  private Long inspectedById;
  private String inspectedByName;

  private LocalDateTime inspectedAt;
}
