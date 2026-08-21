package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.service.ServiceRequestDTO;
import com.example.spring_boot_project_api.dto.response.service.ServiceResponseDTO;

public interface ServiceService {
  ServiceResponseDTO createService(ServiceRequestDTO dto);

  ServiceResponseDTO getServiceById(Long id);

  List<ServiceResponseDTO> getAllServices();

  ServiceResponseDTO updateService(Long id, ServiceRequestDTO dto);

  void deleteService(Long id);
}