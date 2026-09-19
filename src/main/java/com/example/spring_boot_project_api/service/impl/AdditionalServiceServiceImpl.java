package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.response.additional_service.AdditionalServiceResponseDTO;
import com.example.spring_boot_project_api.model.AdditionalService;
import com.example.spring_boot_project_api.repository.AdditionalServiceRepository;
import com.example.spring_boot_project_api.service.AdditionalServiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdditionalServiceServiceImpl implements AdditionalServiceService {
  private final AdditionalServiceRepository additionalServiceRepository;

  @Override
  public List<AdditionalServiceResponseDTO> getActiveServices() {
    return additionalServiceRepository.findByActiveTrue().stream()
        .map(this::toResponse)
        .toList();
  }

  private AdditionalServiceResponseDTO toResponse(AdditionalService s) {
    return new AdditionalServiceResponseDTO(s.getId(), s.getName(), s.getNameKh(), s.getDescription(),
        s.getPricePerDay(), s.getIcon(), s.getActive());
  }
}