package com.example.spring_boot_project_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.spring_boot_project_api.enums.RentalStatusEnum;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_rentals")
public class Rental {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id", nullable = false, unique = true)
  private Reservation reservation;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vehicle_id", nullable = false)
  private Vehicle vehicle;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private User user;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pick_up_location_id", nullable = false)
  private Location pickUpLocation;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "return_location_id", nullable = false)
  private Location returnLocation;

  @NotNull
  @Column(name = "pick_up_date_time", nullable = false)
  private LocalDateTime pickUpDateTime;

  @NotNull
  @Column(name = "expected_return_date_time", nullable = false)
  private LocalDateTime expectedReturnDateTime;

  @Column(name = "actual_return_date_time")
  private LocalDateTime actualReturnDateTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private RentalStatusEnum status = RentalStatusEnum.PENDING;

  @NotNull
  @Column(name = "base_price", nullable = false)
  private BigDecimal basePrice;

  @Column(name = "discount_amount", nullable = false)
  private BigDecimal discountAmount = BigDecimal.ZERO;

  @Column(name = "additional_charges", nullable = false)
  private BigDecimal additionalCharges = BigDecimal.ZERO;

  @Column(name = "late_fee", nullable = false)
  private BigDecimal lateFee = BigDecimal.ZERO;

  @NotNull
  @Column(name = "total_price", nullable = false)
  private BigDecimal totalPrice;

  @Size(max = 255)
  @Column(name = "notes", length = 255)
  private String notes;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}