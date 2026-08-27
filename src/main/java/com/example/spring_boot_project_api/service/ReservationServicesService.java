package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.reservation_service.ReservationServicesRequestDTO;
import com.example.spring_boot_project_api.dto.response.reservation_service.ReservationServicesResponseDTO;

public interface ReservationServicesService {
  ReservationServicesResponseDTO createReservationService(ReservationServicesRequestDTO dto);

  ReservationServicesResponseDTO getReservationServiceById(Long id);

  List<ReservationServicesResponseDTO> getAllReservationServices();

  List<ReservationServicesResponseDTO> getMyReservationServices();

  ReservationServicesResponseDTO updateReservationService(Long id, ReservationServicesRequestDTO dto);

  void deleteReservationService(Long id);
}
