package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.service.ServiceRequestDTO;
import com.example.spring_boot_project_api.dto.response.service.ServiceResponseDTO;
import com.example.spring_boot_project_api.model.Services;
import com.example.spring_boot_project_api.repository.ServiceRepository;
import com.example.spring_boot_project_api.service.ServiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
  private final ServiceRepository serviceRepository;

  @Override
  public ServiceResponseDTO createService(ServiceRequestDTO dto) {
    Services services = new Services();
    services.setName(dto.getName());
    services.setDescription(dto.getDescription());
    services.setPrice(dto.getPrice());
    services.setIsActive(dto.getIsActive());

    Services saved = serviceRepository.save(services);
    return toResponse(saved);
  }

  @Override
  public ServiceResponseDTO getServiceById(Long id) {
    Services services = serviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found"));

    return toResponse(services);
  }

  @Override
  public List<ServiceResponseDTO> getAllServices() {
    return serviceRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public ServiceResponseDTO updateService(Long id, ServiceRequestDTO dto) {
    Services services = serviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found"));

    services.setName(dto.getName());
    services.setDescription(dto.getDescription());
    services.setPrice(dto.getPrice());
    services.setIsActive(dto.getIsActive());

    Services saved = serviceRepository.save(services);
    return toResponse(saved);
  }

  @Override
  public void deleteService(Long id) {
    if (!serviceRepository.existsById(id)) {
      throw new RuntimeException("Service not found");
    }

    serviceRepository.deleteById(id);
  }

  private ServiceResponseDTO toResponse(Services s) {
    return new ServiceResponseDTO(s.getId(), s.getName(), s.getDescription(), s.getPrice(), s.getIsActive());
  }
}
