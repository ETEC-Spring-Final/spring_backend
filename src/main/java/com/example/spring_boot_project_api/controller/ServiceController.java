package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
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
import lombok.RequiredArgsConstructor;

/**
 * Add-on / maintenance services catalogue.
 *
 * FIX (Phase A): this controller previously had NO method security at all.
 * Because SecurityConfig only requires "authenticated" at the URL level, any
 * signed-in CUSTOMER could create, rename or delete entries in the shared
 * services catalogue. Writes are now restricted to staff roles.
 *
 * Reads are intentionally left ungated here and permitted in SecurityConfig
 * (GET /api/services/**) so the booking form and the public landing page can
 * list add-ons without a JWT.
 *
 * Also switched from @Autowired field injection to constructor injection via
 * Lombok, matching the convention in AGENTS.md §2.5.
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

  private final ServiceService serviceService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
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

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public ServiceResponseDTO updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequestDTO dto) {
    return serviceService.updateService(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @DeleteMapping("/{id}")
  public void deleteService(@PathVariable Long id) {
    serviceService.deleteService(id);
  }
}