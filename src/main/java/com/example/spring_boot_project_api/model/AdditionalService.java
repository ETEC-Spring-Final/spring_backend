package com.example.spring_boot_project_api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Rental add-on that is charged PER DAY (e.g. extra driver, insurance).
 * Distinct from the existing Services catalogue (which uses a flat price and
 * is admin-managed for maintenance add-ons). Seeded with 4 defaults.
 */
@Entity
@Table(name = "tb_additional_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalService {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(name = "name", nullable = false, length = 100)
  @Size(max = 100, message = "Service name must be under 100 characters")
  private String name;

  @NotBlank
  @Column(name = "name_kh", nullable = false, length = 100)
  @Size(max = 100, message = "Khmer name must be under 100 characters")
  private String nameKh;

  @NotBlank
  @Column(name = "description", nullable = false, length = 255)
  @Size(max = 255, message = "Description must be under 255 characters")
  private String description;

  @NotNull
  @Column(name = "price_per_day", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerDay;

  // Semantic icon key the frontend maps to an SVG (driver / gps /
  // child-seat / insurance).
  @NotBlank
  @Column(name = "icon", nullable = false, length = 30)
  private String icon;

  @Builder.Default
  @Column(name = "active", nullable = false)
  private Boolean active = true;
}