package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.inspection.InspectionRequestDTO;
import com.example.spring_boot_project_api.dto.response.inspection.InspectionResponseDTO;
import com.example.spring_boot_project_api.enums.InspectionTypeEnum;
import com.example.spring_boot_project_api.service.InspectionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inspections")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
public class InspectionController {

  @Autowired
  private InspectionService inspectionService;

  @PostMapping
  public InspectionResponseDTO createInspection(@Valid @RequestBody InspectionRequestDTO dto) {
    return inspectionService.createInspection(dto);
  }

  @PutMapping("/{id}")
  public InspectionResponseDTO updateInspection(@PathVariable Long id, @Valid @RequestBody InspectionRequestDTO dto) {
    return inspectionService.updateInspection(id, dto);
  }

  @DeleteMapping("/{id}")
  public void deleteInspection(@PathVariable Long id) {
    inspectionService.deleteInspection(id);
  }

  @GetMapping("/{id}")
  public InspectionResponseDTO getInspectionById(@PathVariable Long id) {
    return inspectionService.getInspectionById(id);
  }

  @GetMapping("/rental/{rentalId}")
  public List<InspectionResponseDTO> getInspectionsByRentalId(@PathVariable Long rentalId) {
    return inspectionService.getInspectionsByRentalId(rentalId);
  }

  @GetMapping("/type")
  public Page<InspectionResponseDTO> getInspectionsByType(
      @RequestParam InspectionTypeEnum type,
      @ParameterObject @PageableDefault(size = 8, sort = "inspectedAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return inspectionService.getInspectionsByType(type, pageable);
  }

  @GetMapping
  public Page<InspectionResponseDTO> getAllInspections(
      @ParameterObject @PageableDefault(size = 8, sort = "inspectedAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return inspectionService.getAllInspections(pageable);
  }
}