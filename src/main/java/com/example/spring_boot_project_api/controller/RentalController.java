package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.rental.RentalRequestDTO;
import com.example.spring_boot_project_api.dto.response.rental.RentalResponseDTO;
import com.example.spring_boot_project_api.enums.RentalStatusEnum;
import com.example.spring_boot_project_api.service.RentalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
  @Autowired
  private RentalService rentalService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping
  public RentalResponseDTO createRental(@Valid @RequestBody RentalRequestDTO dto) {
    return rentalService.createRental(dto);
  }

  // Set ownership in impl
  @GetMapping("/{id}")
  public RentalResponseDTO getRentalById(@PathVariable Long id) {
    return rentalService.getRentalById(id);
  }

  @GetMapping("/my-rentals")
  public List<RentalResponseDTO> getMyRentals() {
    return rentalService.getMyRentals();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping
  public List<RentalResponseDTO> getAllRentals() {
    return rentalService.getAllRentals();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public RentalResponseDTO updateRental(@PathVariable Long id, @Valid @RequestBody RentalRequestDTO dto) {
    return rentalService.updateRental(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PatchMapping("/{id}/status")
  public RentalResponseDTO changeRentalStatus(@PathVariable Long id, @RequestParam RentalStatusEnum status) {
    return rentalService.changeRentalStatus(id, status);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @DeleteMapping("/{id}")
  public void deleteRental(@PathVariable Long id) {
    rentalService.deleteRental(id);
  }
}
