package com.example.spring_boot_project_api.dto.request.inspection;

import java.math.BigDecimal;
import java.util.List;

import com.example.spring_boot_project_api.enums.FuelLevelEnum;
import com.example.spring_boot_project_api.enums.InspectionTypeEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InspectionRequestDTO {
  @NotNull
  private Long rentalId;

  @NotNull
  private InspectionTypeEnum type;

  @NotNull
  private FuelLevelEnum fuelLevel;

  @NotNull
  @PositiveOrZero
  private BigDecimal odometer;
  private String conditionNotes;
  private String damageReport;
  private List<String> photos;
}
