package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.service.ServiceRequestDTO;
import com.example.spring_boot_project_api.dto.response.service.ServiceResponseDTO;
import com.example.spring_boot_project_api.service.ServiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
  @Autowired
  private ServiceService serviceService;

  @PostMapping
  public ServiceResponseDTO createService(@Valid @RequestBody ServiceRequestDTO dto) {
    return serviceService.createService(dto);
  }

  @GetMapping("/{id}")
  public ServiceResponseDTO getServiceById(@PathVariable Long id) {
    return serviceService.getServiceById(id);
  }

  @GetMapping
  public List<ServiceResponseDTO> getAllServices() {
    return serviceService.getAllServices();
  }

  @PutMapping("/{id}")
  public ServiceResponseDTO updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequestDTO dto) {
    return serviceService.updateService(id, dto);
  }

  @DeleteMapping("/{id}")
  public void deleteService(@PathVariable Long id) {
    serviceService.deleteService(id);
  }
}
