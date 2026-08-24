package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.maintenance_record.MaintenanceRecordRequestDTO;
import com.example.spring_boot_project_api.dto.response.maintenance_record.MaintenanceRecordResponseDTO;
import com.example.spring_boot_project_api.enums.MaintenanceStatusEnum;
import com.example.spring_boot_project_api.model.MaintenanceRecord;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.model.Vehicle;
import com.example.spring_boot_project_api.repository.MaintenanceRecordRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.repository.VehicleRepository;
import com.example.spring_boot_project_api.service.MaintenanceRecordService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {
  private final MaintenanceRecordRepository maintenanceRecordRepository;
  private final VehicleRepository vehicleRepository;
  private final UserRepository userRepository;

  @Override
  public MaintenanceRecordResponseDTO createMaintenanceRecord(MaintenanceRecordRequestDTO dto) {
    Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    // Inside any service method:
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 1. Get username/email stored from the JWT
    String currentUsername = authentication.getName();

    // 2. Fetch the actual database entity
    User currentUser = userRepository.findByEmail(currentUsername) // returns "johndoe@example.com"
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

    MaintenanceRecord record = new MaintenanceRecord();
    record.setVehicle(vehicle);
    record.setType(dto.getType());
    record.setDescription(dto.getDescription());
    record.setScheduledDate(dto.getScheduledDate());
    record.setCompletedDate(dto.getCompletedDate());
    record.setCost(dto.getCost());
    record.setStatus(dto.getStatus() != null ? dto.getStatus() : MaintenanceStatusEnum.SCHEDULED);
    record.setUser(currentUser);

    MaintenanceRecord saved = maintenanceRecordRepository.save(record);
    return toResponse(saved);
  }

  @Override
  public MaintenanceRecordResponseDTO getMaintenanceRecordById(Long id) {
    MaintenanceRecord record = maintenanceRecordRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Maintenance record not found"));
    return toResponse(record);
  }

  @Override
  public List<MaintenanceRecordResponseDTO> getAllMaintenanceRecords() {
    return maintenanceRecordRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public MaintenanceRecordResponseDTO updateMaintenanceRecord(Long id, MaintenanceRecordRequestDTO dto) {
    MaintenanceRecord record = maintenanceRecordRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Maintenance record not found"));

    Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    record.setVehicle(vehicle);
    record.setType(dto.getType());
    record.setDescription(dto.getDescription());
    record.setScheduledDate(dto.getScheduledDate());
    record.setCompletedDate(dto.getCompletedDate());
    record.setCost(dto.getCost());
    record.setStatus(dto.getStatus());

    MaintenanceRecord updated = maintenanceRecordRepository.save(record);
    return toResponse(updated);
  }

  @Override
  public void deleteMaintenanceRecord(Long id) {
    if (!maintenanceRecordRepository.existsById(id)) {
      throw new RuntimeException("Maintenance record not found");
    }
    maintenanceRecordRepository.deleteById(id);
  }

  private MaintenanceRecordResponseDTO toResponse(MaintenanceRecord mr) {
    return new MaintenanceRecordResponseDTO(mr.getId(), mr.getVehicle().getId(), mr.getType(), mr.getDescription(),
        mr.getScheduledDate(), mr.getCompletedDate(), mr.getCost(), mr.getStatus(), mr.getUser().getId(),
        mr.getCreatedAt(), mr.getUpdatedAt());
  }
}
