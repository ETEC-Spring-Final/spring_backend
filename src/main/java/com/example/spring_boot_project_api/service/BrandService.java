package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.brand.BrandRequestDTO;
import com.example.spring_boot_project_api.dto.response.brand.BrandResponseDTO;

public interface BrandService {
  BrandResponseDTO createBrand(BrandRequestDTO dto);

  List<BrandResponseDTO> getAllBrands();

  BrandResponseDTO getBrandById(Long id);

  BrandResponseDTO updateBrand(Long id, BrandRequestDTO dto);

  void deleteBrand(Long id);
}
