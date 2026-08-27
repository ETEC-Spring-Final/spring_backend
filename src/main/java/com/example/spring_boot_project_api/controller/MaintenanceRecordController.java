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

import com.example.spring_boot_project_api.dto.request.maintenance_record.MaintenanceRecordRequestDTO;
import com.example.spring_boot_project_api.dto.response.maintenance_record.MaintenanceRecordResponseDTO;
import com.example.spring_boot_project_api.service.MaintenanceRecordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/maintenace-records")
public class MaintenanceRecordController {
  @Autowired
  private MaintenanceRecordService maintenanceRecordService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping
  public MaintenanceRecordResponseDTO createMaintenanceRecord(@Valid @RequestBody MaintenanceRecordRequestDTO dto) {
    return maintenanceRecordService.createMaintenanceRecord(dto);
  }

  @GetMapping("/{id}")
  public MaintenanceRecordResponseDTO getMaintenanceRecordById(@PathVariable Long id) {
    return maintenanceRecordService.getMaintenanceRecordById(id);
  }

  @GetMapping
  public List<MaintenanceRecordResponseDTO> getAllMaintenanceRecords() {
    return maintenanceRecordService.getAllMaintenanceRecords();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public MaintenanceRecordResponseDTO updateMaintenanceRecord(@PathVariable Long id,
      @Valid @RequestBody MaintenanceRecordRequestDTO dto) {
    return maintenanceRecordService.updateMaintenanceRecord(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @DeleteMapping("/{id}")
  public void deleteMaintenanceRecord(@PathVariable Long id) {
    maintenanceRecordService.deleteMaintenanceRecord(id);
  }
}
