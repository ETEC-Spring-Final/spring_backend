package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.brand.BrandRequestDTO;
import com.example.spring_boot_project_api.dto.response.brand.BrandResponseDTO;
import com.example.spring_boot_project_api.service.BrandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
  private final BrandService brandService;

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  public ResponseEntity<BrandResponseDTO> createBrand(@Valid @RequestBody BrandRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(brandService.createBrand(dto));
  }

  @GetMapping
  public ResponseEntity<List<BrandResponseDTO>> getAllBrands() {
    return ResponseEntity.ok(brandService.getAllBrands());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BrandResponseDTO> getBrandById(@PathVariable Long id) {
    return ResponseEntity.ok(brandService.getBrandById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  public ResponseEntity<BrandResponseDTO> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequestDTO dto) {
    return ResponseEntity.ok(brandService.updateBrand(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  public void deleteBrand(@PathVariable Long id) {
    brandService.deleteBrand(id);
  }
}