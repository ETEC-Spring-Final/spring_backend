package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.rental.RentalRequestDTO;
import com.example.spring_boot_project_api.dto.response.rental.RentalResponseDTO;
import com.example.spring_boot_project_api.enums.RentalStatusEnum;

public interface RentalService {
  RentalResponseDTO createRental(RentalRequestDTO dto);

  RentalResponseDTO getRentalById(Long id);

  List<RentalResponseDTO> getMyRentals();

  List<RentalResponseDTO> getAllRentals();

  RentalResponseDTO updateRental(Long id, RentalRequestDTO dto);

  RentalResponseDTO changeRentalStatus(Long id, RentalStatusEnum status);

  void deleteRental(Long id);
}
