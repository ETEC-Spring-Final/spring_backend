package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.ReservationAdditionalService;

public interface ReservationAdditionalServiceRepository extends JpaRepository<ReservationAdditionalService, Long> {
  List<ReservationAdditionalService> findByReservationId(Long reservationId);

  void deleteByReservationId(Long reservationId);
}