package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.VehicleImage;

public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {
  List<VehicleImage> findByVehicleId(Long vehicleId);
}
