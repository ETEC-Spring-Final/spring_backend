package com.example.spring_boot_project_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.spring_boot_project_api.enums.FuelLevelEnum;
import com.example.spring_boot_project_api.enums.InspectionTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_inspection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inspection {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_id", nullable = false)
  private Rental rental;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private InspectionTypeEnum type;

  @Column(name = "fuel_level")
  private FuelLevelEnum fuelLevel;

  @Column(name = "odometer", precision = 10, scale = 2)
  private BigDecimal odometer;

  @Column(name = "condition_notes")
  private String conditionNotes;

  @Column(name = "damage_report")
  private String damageReport;

  @Column(name = "photos", length = 255)
  private List<String> photos;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inspected_by", nullable = false)
  private User user;

  @CreationTimestamp
  @Column(name = "inspected_at", updatable = false)
  private LocalDateTime inspectedAt;
}
