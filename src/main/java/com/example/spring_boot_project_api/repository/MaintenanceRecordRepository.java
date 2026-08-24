package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.enums.MaintenanceStatusEnum;
import com.example.spring_boot_project_api.enums.MaintenanceTypeEnum;
import com.example.spring_boot_project_api.model.MaintenanceRecord;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

  // Find all maintenance records by status (e.g., SCHEDULED, COMPLETED)
  List<MaintenanceRecord> findByStatus(MaintenanceStatusEnum status);

  // Find all maintenance records by type (e.g., OIL_CHANGE, REPAIR)
  List<MaintenanceRecord> findByType(MaintenanceTypeEnum type);

  // Find all maintenance records for a specific vehicle ID
  List<MaintenanceRecord> findByVehicleId(Long vehicleId);
}