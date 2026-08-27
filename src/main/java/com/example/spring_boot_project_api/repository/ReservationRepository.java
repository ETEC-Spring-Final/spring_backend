package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.enums.ReservationStatusEnum;
import com.example.spring_boot_project_api.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  List<Reservation> findByStatus(ReservationStatusEnum status);

  List<Reservation> findByVehicleId(Long vehicleId);

  List<Reservation> findByUserId(Long userId);
}
