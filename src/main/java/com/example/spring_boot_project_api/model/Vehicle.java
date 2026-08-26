package com.example.spring_boot_project_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.spring_boot_project_api.enums.CarTypeEnum;
import com.example.spring_boot_project_api.enums.FuelTypeEnum;
import com.example.spring_boot_project_api.enums.StatusEnum;
import com.example.spring_boot_project_api.enums.TransmissionEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(name = "brand", nullable = false, length = 50)
  @Size(max = 50, message = "Brand must be under 50 characters")
  private String brand;

  @NotBlank
  @Column(name = "model", nullable = false, length = 50)
  @Size(max = 50, message = "Model must be under 50 characters")
  private String model;

  @NotNull
  @Column(name = "year_of_manufacture")
  private Integer yearOfManufacture;

  @NotBlank
  @Column(name = "license_plate", nullable = false)
  private String licensePlate;

  @NotBlank
  @Column(name = "color", nullable = false)
  private String color;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private CarTypeEnum type;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "transmission", nullable = false)
  private TransmissionEnum transmission;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "fuel_type", nullable = false)
  private FuelTypeEnum fuelType;

  @NotNull
  @Column(name = "seats", nullable = false)
  private Integer seats;

  @NotNull
  @Column(name = "price_per_day", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerDay;

  @NotNull
  @Column(name = "mile_age", nullable = false, precision = 10, scale = 2)
  private Double mileAge;

  @NotBlank
  @Column(name = "description", nullable = false)
  private String description;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private StatusEnum status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
