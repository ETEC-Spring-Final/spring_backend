package com.example.spring_boot_project_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.spring_boot_project_api.enums.ReservationStatusEnum;
import com.example.spring_boot_project_api.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  List<Reservation> findByStatus(ReservationStatusEnum status);

  List<Reservation> findByVehicleId(Long vehicleId);

  List<Reservation> findByUserId(Long userId);

  // Overlapping reservations for the same vehicle across the requested window,
  // excluding cancelled ones (a cancelled booking frees the slot).
  @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
      "WHERE r.vehicle.id = :vehicleId " +
      "AND r.status <> :excludedStatus " +
      "AND r.pickUpDateTime < :returnDateTime " +
      "AND r.returnDateTime > :pickUpDateTime")
  boolean hasOverlappingReservation(@Param("vehicleId") Long vehicleId,
      @Param("excludedStatus") ReservationStatusEnum excludedStatus,
      @Param("pickUpDateTime") LocalDateTime pickUpDateTime,
      @Param("returnDateTime") LocalDateTime returnDateTime);

  // Active reservations for a vehicle that still occupy the calendar:
  // not cancelled, and the window hasn't fully ended yet (returnDateTime >= now).
  @Query("SELECT r FROM Reservation r " +
      "WHERE r.vehicle.id = :vehicleId " +
      "AND r.status <> :excludedStatus " +
      "AND r.returnDateTime >= :now")
  List<Reservation> findActiveBookings(@Param("vehicleId") Long vehicleId,
      @Param("excludedStatus") ReservationStatusEnum excludedStatus,
      @Param("now") LocalDateTime now);
}