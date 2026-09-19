package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.response.additional_service.AdditionalServiceResponseDTO;

public interface AdditionalServiceService {
  List<AdditionalServiceResponseDTO> getActiveServices();
}