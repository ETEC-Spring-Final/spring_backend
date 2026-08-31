package com.example.spring_boot_project_api.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.rental_document.RentalDocumentRequestDTO;
import com.example.spring_boot_project_api.dto.response.rental_document.RentalDocumentResponseDTO;
import com.example.spring_boot_project_api.enums.DocumentTypeEnum;

public interface RentalDocumentService {
  RentalDocumentResponseDTO createRentalDocument(RentalDocumentRequestDTO dto);

  RentalDocumentResponseDTO uploadDocument(Long rentalId, MultipartFile file, DocumentTypeEnum documentType);

  RentalDocumentResponseDTO getRentalDocumentById(Long id);

  List<RentalDocumentResponseDTO> getMyRentalDocuments();

  List<RentalDocumentResponseDTO> getAllRentalDocuments();

  List<RentalDocumentResponseDTO> getDocumentsByRentalId(Long rentalId);

  RentalDocumentResponseDTO updateRentalDocument(Long id, DocumentTypeEnum documentType);

  void deleteRentalDocument(Long id);
}
