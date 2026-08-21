package com.example.spring_boot_project_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.DiscountTypeEnum;

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
import lombok.Data;

@Data
@Entity
@Table(name = "tb_discounts")
public class Discount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(name = "code", nullable = false)
  private String code;

  @NotBlank
  @Column(name = "description", nullable = false, length = 255)
  @Size(max = 255, message = "Description should be under 255 characters")
  private String description;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private DiscountTypeEnum type;

  @NotNull
  @Column(name = "value", nullable = false)
  private BigDecimal value;

  @NotNull
  @Column(name = "valid_from", nullable = false)
  private LocalDateTime validFrom;

  @Column(name = "valid_to")
  private LocalDateTime validTo;

  @Column(name = "max_uses")
  private Integer maxUses;

  @Column(name = "used_count", nullable = false)
  private Integer usedCount = 0;

  @Column(name = "is_active")
  private Boolean isActive = true;
}
