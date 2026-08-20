package com.example.spring_boot_project_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
  List<Location> findByCity(String city);

  List<Location> findByIsActive(Boolean isActive);

  Optional<Location> findByAddress(String address);
}
