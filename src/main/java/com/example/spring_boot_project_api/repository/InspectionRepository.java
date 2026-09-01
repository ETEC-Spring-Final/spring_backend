package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.enums.InspectionTypeEnum;
import com.example.spring_boot_project_api.model.Inspection;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {
  Page<Inspection> findByType(InspectionTypeEnum type, Pageable pageable);

  List<Inspection> findByRentalId(Long rentalId);

  boolean existsByRentalIdAndType(Long rentalId, InspectionTypeEnum type);
}
