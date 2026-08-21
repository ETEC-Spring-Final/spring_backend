package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.location.LocationRequestDTO;
import com.example.spring_boot_project_api.dto.response.location.LocationResponseDTO;
import com.example.spring_boot_project_api.service.LocationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
  @Autowired
  private LocationService locationService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping
  public LocationResponseDTO createLocation(@Valid @RequestBody LocationRequestDTO dto) {
    return locationService.createLocation(dto);
  }

  @GetMapping("/{id}")
  public LocationResponseDTO getLocationById(@PathVariable Long id) {
    return locationService.getLocationById(id);
  }

  @GetMapping
  public List<LocationResponseDTO> getAllLocation() {
    return locationService.getAllLocations();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public LocationResponseDTO updateLocation(@PathVariable Long id, @Valid @RequestBody LocationRequestDTO dto) {
    return locationService.updateLocation(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @DeleteMapping("/{id}")
  public void deleteLocation(@PathVariable Long id) {
    locationService.deleteLocation(id);
  }
}
