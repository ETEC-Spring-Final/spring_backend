package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.rental_document.RentalDocumentRequestDTO;
import com.example.spring_boot_project_api.dto.response.attachment.AttachmentResponseDTO;
import com.example.spring_boot_project_api.dto.response.rental_document.RentalDocumentResponseDTO;
import com.example.spring_boot_project_api.enums.DocumentTypeEnum;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.Attachment;
import com.example.spring_boot_project_api.model.Rental;
import com.example.spring_boot_project_api.model.RentalDocument;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.AttachmentRepository;
import com.example.spring_boot_project_api.repository.RentalDocumentRepository;
import com.example.spring_boot_project_api.repository.RentalRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.AttachmentService;
import com.example.spring_boot_project_api.service.RentalDocumentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalDocumentServiceImpl implements RentalDocumentService {
  private final RentalDocumentRepository rentalDocumentRepository;
  private final RentalRepository rentalRepository;
  private final AttachmentRepository attachmentRepository;
  private final AttachmentService attachmentService;
  private final UserRepository userRepository;

  @Override
  public RentalDocumentResponseDTO createRentalDocument(RentalDocumentRequestDTO dto) {
    Rental rental = rentalRepository.findById(dto.getRentalId())
        .orElseThrow(() -> new RuntimeException("Rental not found"));

    Attachment attachment = attachmentRepository.findById(dto.getAttachmentId())
        .orElseThrow(() -> new RuntimeException("Attachment not found"));

    RentalDocument rentalDocument = new RentalDocument();
    rentalDocument.setRental(rental);
    rentalDocument.setAttachment(attachment);

    RentalDocument saved = rentalDocumentRepository.save(rentalDocument);
    return toResponse(saved);
  }

  @Override
  public RentalDocumentResponseDTO uploadDocument(Long rentalId, MultipartFile file, DocumentTypeEnum documentType) {
    Rental rental = rentalRepository.findById(rentalId)
        .orElseThrow(() -> new RuntimeException("Rental not found"));

    User currentUser = getCurrentUser();
    boolean isOwner = rental.getUser().getId().equals(currentUser.getId());
    boolean isStaff = currentUser.getRole() != RoleEnum.CUSTOMER;

    if (!isOwner && !isStaff) {
      throw new RuntimeException("This rental does not belong to you");
    }

    AttachmentResponseDTO attachmentDto = attachmentService.uploadAttachment(file, documentType);
    Attachment attachment = attachmentRepository.findById(attachmentDto.getId())
        .orElseThrow(() -> new RuntimeException("Attachment not found"));

    RentalDocument rentalDocument = new RentalDocument();
    rentalDocument.setRental(rental);
    rentalDocument.setAttachment(attachment);

    RentalDocument saved = rentalDocumentRepository.save(rentalDocument);
    return toResponse(saved);
  }

  @Override
  public RentalDocumentResponseDTO getRentalDocumentById(Long id) {
    RentalDocument rentalDocument = rentalDocumentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rental document not found"));

    return toResponse(rentalDocument);
  }

  @Override
  public List<RentalDocumentResponseDTO> getMyRentalDocuments() {
    User currentUser = getCurrentUser();
    List<Rental> myRentals = rentalRepository.findByUserId(currentUser.getId());
    List<Long> rentalIds = myRentals.stream().map(Rental::getId).toList();

    return rentalDocumentRepository.findByRentalIdIn(rentalIds).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public List<RentalDocumentResponseDTO> getAllRentalDocuments() {
    return rentalDocumentRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public List<RentalDocumentResponseDTO> getDocumentsByRentalId(Long rentalId) {
    return rentalDocumentRepository.findByRentalId(rentalId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public RentalDocumentResponseDTO updateRentalDocument(Long id, DocumentTypeEnum documentType) {
    RentalDocument rentalDocument = rentalDocumentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rental document not found"));

    Attachment attachment = rentalDocument.getAttachment();
    attachment.setDocumentType(documentType);
    attachmentRepository.save(attachment);

    return toResponse(rentalDocument);
  }

  @Override
  public void deleteRentalDocument(Long id) {
    if (!rentalDocumentRepository.existsById(id)) {
      throw new RuntimeException("Rental document not found");
    }
    rentalDocumentRepository.deleteById(id);
  }

  private RentalDocumentResponseDTO toResponse(RentalDocument rd) {
    AttachmentResponseDTO attachmentDto = new AttachmentResponseDTO(
        rd.getAttachment().getId(), rd.getAttachment().getFileUrl(), rd.getAttachment().getDocumentType(),
        rd.getAttachment().getIsPrimary(), rd.getAttachment().getDisplayOrder(), rd.getAttachment().getUploadedAt());

    return new RentalDocumentResponseDTO(rd.getId(), rd.getRental().getId(), attachmentDto);
  }

  // Ownership function
  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    return userRepository.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
  }
}