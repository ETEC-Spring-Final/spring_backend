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

import com.example.spring_boot_project_api.dto.request.vehicle.VehicleRequestDTO;
import com.example.spring_boot_project_api.dto.response.vehicle.VehicleResponseDTO;
import com.example.spring_boot_project_api.service.VehicleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
  @Autowired
  private VehicleService vehicleService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public VehicleResponseDTO createVehicle(@Valid @RequestBody VehicleRequestDTO dto) {
    return vehicleService.createVehicle(dto);
  }

  @GetMapping("/{id}")
  public VehicleResponseDTO getVehicleById(@PathVariable Long id) {
    return vehicleService.getVehicleById(id);
  }

  @GetMapping
  public List<VehicleResponseDTO> getAllVehicles() {
    return vehicleService.getAllVehicles();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public VehicleResponseDTO updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequestDTO dto) {
    return vehicleService.updateVehicle(id, dto);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public void deleteVehicle(@PathVariable Long id) {
    vehicleService.deleteVehicle(id);
  }
}
