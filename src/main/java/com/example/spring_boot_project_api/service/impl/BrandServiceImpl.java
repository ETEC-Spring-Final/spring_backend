package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.brand.BrandRequestDTO;
import com.example.spring_boot_project_api.dto.response.brand.BrandResponseDTO;
import com.example.spring_boot_project_api.model.Brand;
import com.example.spring_boot_project_api.repository.BrandRepository;
import com.example.spring_boot_project_api.service.BrandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
  private final BrandRepository brandRepository;

  @Override
  public BrandResponseDTO createBrand(BrandRequestDTO dto) {
    if (brandRepository.existsByName(dto.getName())) {
      throw new RuntimeException("Brand name already existed");
    }

    Brand brand = new Brand();
    brand.setName(dto.getName());

    Brand saved = brandRepository.save(brand);
    return toResponse(saved);
  }

  @Override
  public BrandResponseDTO getBrandById(Long id) {
    Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));

    return toResponse(brand);
  }

  @Override
  public List<BrandResponseDTO> getAllBrands() {
    return brandRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Override
  public BrandResponseDTO updateBrand(Long id, BrandRequestDTO dto) {
    Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));

    brandRepository.findByName(dto.getName()).ifPresent(existing -> {
      if (!existing.getId().equals(id)) {
        throw new RuntimeException("Brand name already exists");
      }
    });

    brand.setName(dto.getName());

    Brand saved = brandRepository.save(brand);
    return toResponse(saved);
  }

  @Override
  public void deleteBrand(Long id) {
    if (!brandRepository.existsById(id)) {
      throw new RuntimeException("Brand not found");
    }
    brandRepository.deleteById(id);
  }

  private BrandResponseDTO toResponse(Brand b) {
    return new BrandResponseDTO(b.getId(), b.getName());
  }
}
