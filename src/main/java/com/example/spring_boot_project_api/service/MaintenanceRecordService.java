package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.maintenance_record.MaintenanceRecordRequestDTO;
import com.example.spring_boot_project_api.dto.response.maintenance_record.MaintenanceRecordResponseDTO;

public interface MaintenanceRecordService {
  MaintenanceRecordResponseDTO createMaintenanceRecord(MaintenanceRecordRequestDTO dto);

  MaintenanceRecordResponseDTO getMaintenanceRecordById(Long id);

  List<MaintenanceRecordResponseDTO> getAllMaintenanceRecords();

  MaintenanceRecordResponseDTO updateMaintenanceRecord(Long id, MaintenanceRecordRequestDTO dto);

  void deleteMaintenanceRecord(Long id);
}
