package com.example.spring_boot_project_api.dto.response.vehicle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.CarTypeEnum;
import com.example.spring_boot_project_api.enums.FuelTypeEnum;
import com.example.spring_boot_project_api.enums.StatusEnum;
import com.example.spring_boot_project_api.enums.TransmissionEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleResponseDTO {
  private Long id;
  private String brand;
  private String model;
  private Integer yearOfManufacture;
  private String licensePlate;
  private String color;
  private CarTypeEnum type;
  private TransmissionEnum transmission;
  private FuelTypeEnum fuelType;
  private Integer seats;
  private BigDecimal pricePerDay;
  private BigDecimal mileAge;
  private String description;
  private StatusEnum status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
