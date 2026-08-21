package com.example.spring_boot_project_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.enums.CarTypeEnum;
import com.example.spring_boot_project_api.enums.StatusEnum;
import com.example.spring_boot_project_api.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
  List<Vehicle> findByStatus(StatusEnum status);

  List<Vehicle> findByType(CarTypeEnum type);

  Optional<Vehicle> findByLicensePlate(String licensePlate);
}
