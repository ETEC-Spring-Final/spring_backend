package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.reservation.ReservationRequestDTO;
import com.example.spring_boot_project_api.dto.response.reservation.ReservationResponseDTO;
import com.example.spring_boot_project_api.enums.ReservationStatusEnum;

public interface ReservationService {
  ReservationResponseDTO createReservation(ReservationRequestDTO dto);

  ReservationResponseDTO getReservationById(Long id);

  // For ADMIN/MANAGER/STAFF
  List<ReservationResponseDTO> getAllReservations();

  // For CUSTOMER in their account
  List<ReservationResponseDTO> getMyReservations();

  ReservationResponseDTO changeReservationStatus(Long id, ReservationStatusEnum status);

  ReservationResponseDTO cancelReservation(Long id);

  ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO dto);

  void deleteReservation(Long id);
}
