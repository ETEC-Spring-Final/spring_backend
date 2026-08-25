package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.reservation.ReservationRequestDTO;
import com.example.spring_boot_project_api.dto.response.reservation.ReservationResponseDTO;
import com.example.spring_boot_project_api.enums.ReservationStatusEnum;
import com.example.spring_boot_project_api.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
  @Autowired
  private ReservationService reservationService;

  // TODO: check for overlapping reservations on the same vehicle before saving
  // (prevent double-booking)
  @PostMapping
  public ReservationResponseDTO createReservation(@Valid @RequestBody ReservationRequestDTO dto) {
    return reservationService.createReservation(dto);
  }

  @GetMapping("/{id}")
  public ReservationResponseDTO getReservationById(@PathVariable Long id) {
    return reservationService.getReservationById(id);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'STAFF')")
  @GetMapping
  public List<ReservationResponseDTO> getAllReservations() {
    return reservationService.getAllReservations();
  }

  @GetMapping("/my-reservations")
  public List<ReservationResponseDTO> getMyReservations() {
    return reservationService.getMyReservations();
  }

  @PatchMapping("/{id}/status")
  public ReservationResponseDTO changeReservationStatus(@PathVariable Long id,
      @RequestParam ReservationStatusEnum status) {
    return reservationService.changeReservationStatus(id, status);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public ReservationResponseDTO updateReservation(@PathVariable Long id,
      @Valid @RequestBody ReservationRequestDTO dto) {
    return reservationService.updateReservation(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'STAFF')")
  @DeleteMapping("/{id}")
  public void deleteReservation(@PathVariable Long id) {
    reservationService.deleteReservation(id);
  }
}
