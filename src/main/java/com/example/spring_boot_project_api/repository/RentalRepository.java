package com.example.spring_boot_project_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.enums.RentalStatusEnum;
import com.example.spring_boot_project_api.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
  List<Rental> findByUserId(Long userId);

  List<Rental> findByVehicleId(Long vehicleId);

  Optional<Rental> findByReservationId(Long reservationId);

  List<Rental> findByStatus(RentalStatusEnum status);
}
