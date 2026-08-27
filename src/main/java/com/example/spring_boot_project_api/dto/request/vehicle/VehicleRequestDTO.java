package com.example.spring_boot_project_api.dto.request.vehicle;

import java.math.BigDecimal;

import com.example.spring_boot_project_api.enums.CarTypeEnum;
import com.example.spring_boot_project_api.enums.FuelTypeEnum;
import com.example.spring_boot_project_api.enums.StatusEnum;
import com.example.spring_boot_project_api.enums.TransmissionEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleRequestDTO {
  @NotBlank
  @Size(max = 50)
  private String brand;

  @NotBlank
  @Size(max = 50)
  private String model;

  @NotNull
  private Integer yearOfManufacture;

  @NotBlank
  private String licensePlate;

  @NotBlank
  private String color;

  @NotNull
  private CarTypeEnum type;

  @NotNull
  private TransmissionEnum transmission;

  @NotNull
  private FuelTypeEnum fuelType;

  @NotNull
  private Integer seats;

  @NotNull
  private BigDecimal pricePerDay;

  @NotNull
  private BigDecimal mileAge;

  @NotBlank
  private String description;

  @NotNull
  private StatusEnum status;
}