package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.AdditionalService;

public interface AdditionalServiceRepository extends JpaRepository<AdditionalService, Long> {
  List<AdditionalService> findByActiveTrue();

  long countByActiveTrue();
}