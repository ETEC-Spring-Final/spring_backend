package com.example.spring_boot_project_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
  Optional<Brand> findByName(String name);

  boolean existsByName(String name);
}
