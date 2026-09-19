package com.example.spring_boot_project_api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Links a selected per-day add-on to a reservation, snapshotting the price
 * per day that applied at booking time.
 */
@Entity
@Table(name = "tb_reservation_additional_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationAdditionalService {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "additional_service_id", nullable = false)
  private AdditionalService additionalService;

  @NotNull
  @Column(name = "price_per_day_at_booking", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePerDayAtBooking;

  @Builder.Default
  @Min(1)
  @Column(name = "quantity")
  private Integer quantity = 1;
}