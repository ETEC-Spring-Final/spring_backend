package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.inspection.InspectionRequestDTO;
import com.example.spring_boot_project_api.dto.response.inspection.InspectionResponseDTO;
import com.example.spring_boot_project_api.enums.InspectionTypeEnum;
import com.example.spring_boot_project_api.enums.RentalStatusEnum;
import com.example.spring_boot_project_api.model.Inspection;
import com.example.spring_boot_project_api.model.Rental;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.InspectionRepository;
import com.example.spring_boot_project_api.repository.RentalRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.InspectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {
  private final InspectionRepository inspectionRepository;
  private final UserRepository userRepository;
  private final RentalRepository rentalRepository;

  @Override
  public InspectionResponseDTO createInspection(InspectionRequestDTO dto) {
    Rental rental = rentalRepository.findById(dto.getRentalId())
        .orElseThrow(() -> new RuntimeException("Rental not found with id: " + dto.getRentalId()));

    // 1. Prevent duplicate inspection of the same type for a rental
    if (inspectionRepository.existsByRentalIdAndType(dto.getRentalId(), dto.getType())) {
      throw new RuntimeException("An inspection of type " + dto.getType() + " already exists for this rental");
    }

    // 2. Validate current rental status before allowing inspection
    if (dto.getType() == InspectionTypeEnum.PICK_UP) {
      if (rental.getStatus() != RentalStatusEnum.PENDING && rental.getStatus() != RentalStatusEnum.CONFIRMED) {
        throw new RuntimeException("Cannot perform PICKUP inspection on a rental that is " + rental.getStatus());
      }
      rental.setStatus(RentalStatusEnum.PICKED_UP); // Or ACTIVE, depending on your preferred lifecycle
    } else if (dto.getType() == InspectionTypeEnum.RETURN) {
      if (rental.getStatus() != RentalStatusEnum.PICKED_UP && rental.getStatus() != RentalStatusEnum.ACTIVE) {
        throw new RuntimeException("Cannot perform RETURN inspection on a rental that has not been picked up");
      }
      rental.setStatus(RentalStatusEnum.RETURNED);
    }

    // 3. Build & save inspection
    Inspection inspection = new Inspection();
    inspection.setRental(rental);
    inspection.setType(dto.getType());
    inspection.setFuelLevel(dto.getFuelLevel());
    inspection.setOdometer(dto.getOdometer());
    inspection.setConditionNotes(dto.getConditionNotes());
    inspection.setDamageReport(dto.getDamageReport());
    inspection.setPhotos(dto.getPhotos());
    inspection.setUser(getCurrentUser());

    Inspection saved = inspectionRepository.save(inspection);

    // Save updated rental status
    rentalRepository.save(rental);

    return toResponse(saved);
  }

  @Override
  public InspectionResponseDTO getInspectionById(Long id) {
    Inspection inspection = inspectionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Inspection not found"));

    return toResponse(inspection);
  }

  @Override
  public Page<InspectionResponseDTO> getAllInspections(Pageable pageable) {
    return inspectionRepository.findAll(pageable).map(this::toResponse);
  }

  @Override
  public Page<InspectionResponseDTO> getInspectionsByType(InspectionTypeEnum type, Pageable pageable) {
    return inspectionRepository.findByType(type, pageable).map(this::toResponse);
  }

  @Override
  public List<InspectionResponseDTO> getInspectionsByRentalId(Long rentalId) {
    return inspectionRepository.findByRentalId(rentalId).stream().map(this::toResponse).toList();
  }

  @Override
  public InspectionResponseDTO updateInspection(Long id, InspectionRequestDTO dto) {
    Inspection inspection = inspectionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Inspection not found"));

    inspection.setFuelLevel(dto.getFuelLevel());
    inspection.setOdometer(dto.getOdometer());
    inspection.setConditionNotes(dto.getConditionNotes());
    inspection.setDamageReport(dto.getDamageReport());
    inspection.setPhotos(dto.getPhotos());

    Inspection saved = inspectionRepository.save(inspection);

    return toResponse(saved);
  }

  @Override
  public void deleteInspection(Long id) {
    if (!inspectionRepository.existsById(id)) {
      throw new RuntimeException("Inspection not found");
    }

    inspectionRepository.deleteById(id);
  }

  private InspectionResponseDTO toResponse(Inspection i) {
    String fullName = (i.getUser().getFirstName() + " " + i.getUser().getLastName()).trim();

    return InspectionResponseDTO.builder().id(i.getId()).rentalId(i.getRental().getId())
        .type(i.getType())
        .fuelLevel(i.getFuelLevel())
        .odometer(i.getOdometer())
        .conditionNotes(i.getConditionNotes())
        .damageReport(i.getDamageReport())
        .photos(i.getPhotos())
        .inspectedById(i.getUser().getId())
        .inspectedByName(fullName)
        .inspectedAt(i.getInspectedAt())
        .build();
  }

  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    return userRepository.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
  }
}
