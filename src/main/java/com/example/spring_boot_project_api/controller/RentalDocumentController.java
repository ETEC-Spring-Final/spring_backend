package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.rental_document.RentalDocumentRequestDTO;
import com.example.spring_boot_project_api.dto.response.rental_document.RentalDocumentResponseDTO;
import com.example.spring_boot_project_api.enums.DocumentTypeEnum;
import com.example.spring_boot_project_api.service.RentalDocumentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rental-documents")
public class RentalDocumentController {
  @Autowired
  private RentalDocumentService rentalDocumentService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping
  public RentalDocumentResponseDTO createRentalDocument(@Valid @RequestBody RentalDocumentRequestDTO dto) {
    return rentalDocumentService.createRentalDocument(dto);
  }

  @PostMapping(value = "/{rentalId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public RentalDocumentResponseDTO uploadDocument(@PathVariable Long rentalId,
      @RequestParam("file") MultipartFile file,
      @RequestParam DocumentTypeEnum documentType) {
    return rentalDocumentService.uploadDocument(rentalId, file, documentType);
  }

  @GetMapping("/my-rental-document")
  public List<RentalDocumentResponseDTO> getMyRentalDocuments() {
    return rentalDocumentService.getMyRentalDocuments();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping("/{id}")
  public RentalDocumentResponseDTO getRentalDocumentById(@PathVariable Long id) {
    return rentalDocumentService.getRentalDocumentById(id);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping("/rental/{rentalId}")
  public List<RentalDocumentResponseDTO> getDocumentsByRentalId(@PathVariable Long rentalId) {
    return rentalDocumentService.getDocumentsByRentalId(rentalId);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping
  public List<RentalDocumentResponseDTO> getAllRentalDocuments() {
    return rentalDocumentService.getAllRentalDocuments();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PatchMapping("/{id}")
  public RentalDocumentResponseDTO updateRentalDocument(@PathVariable Long id,
      @RequestParam DocumentTypeEnum documentType) {
    return rentalDocumentService.updateRentalDocument(id, documentType);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @DeleteMapping("/{id}")
  public void deleteRentalDocument(@PathVariable Long id) {
    rentalDocumentService.deleteRentalDocument(id);
  }
}