package com.example.spring_boot_project_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.spring_boot_project_api.dto.request.inspection.InspectionRequestDTO;
import com.example.spring_boot_project_api.dto.response.inspection.InspectionResponseDTO;
import com.example.spring_boot_project_api.enums.InspectionTypeEnum;

public interface InspectionService {
  InspectionResponseDTO createInspection(InspectionRequestDTO dto);

  InspectionResponseDTO getInspectionById(Long id);

  Page<InspectionResponseDTO> getAllInspections(Pageable pageable);

  Page<InspectionResponseDTO> getInspectionsByType(InspectionTypeEnum type, Pageable pageable);

  List<InspectionResponseDTO> getInspectionsByRentalId(Long rentalId);

  InspectionResponseDTO updateInspection(Long id, InspectionRequestDTO dto);

  void deleteInspection(Long id);
}
