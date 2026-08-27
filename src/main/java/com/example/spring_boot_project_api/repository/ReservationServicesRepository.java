package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.ReservationServices;

public interface ReservationServicesRepository extends JpaRepository<ReservationServices, Long> {
  List<ReservationServices> findByReservationId(Long reservationId);

  List<ReservationServices> findByServiceId(Long serviceId);

  List<ReservationServices> findByReservationUserId(Long userId);
}
