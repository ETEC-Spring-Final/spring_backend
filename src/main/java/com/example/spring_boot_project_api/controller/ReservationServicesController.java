package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.reservation_service.ReservationServicesRequestDTO;
import com.example.spring_boot_project_api.dto.response.reservation_service.ReservationServicesResponseDTO;
import com.example.spring_boot_project_api.service.ReservationServicesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservation-services")
public class ReservationServicesController {
  @Autowired
  private ReservationServicesService reservationServicesService;

  @PostMapping
  public ReservationServicesResponseDTO createReservationService(
      @Valid @RequestBody ReservationServicesRequestDTO dto) {
    return reservationServicesService.createReservationService(dto);
  }

  @GetMapping("/{id}")
  public ReservationServicesResponseDTO getReservationServiceById(@PathVariable Long id) {
    return reservationServicesService.getReservationServiceById(id);
  }

  @GetMapping("/my-reservation-services")
  public List<ReservationServicesResponseDTO> getMyReservationServices() {
    return reservationServicesService.getMyReservationServices();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping
  public List<ReservationServicesResponseDTO> getAllReservationServices() {
    return reservationServicesService.getAllReservationServices();
  }

  @PutMapping("/{id}")
  public ReservationServicesResponseDTO updateReservationService(@PathVariable Long id,
      @Valid @RequestBody ReservationServicesRequestDTO dto) {
    return reservationServicesService.updateReservationService(id, dto);
  }

  @DeleteMapping("/{id}")
  public void deleteReservationService(@PathVariable Long id) {
    reservationServicesService.deleteReservationService(id);
  }
}
