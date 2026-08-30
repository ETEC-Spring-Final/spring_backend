package com.example.spring_boot_project_api.service.impl;

import static com.example.spring_boot_project_api.config.UploadConfig.ALLOWED_EXTENSIONS;
import static com.example.spring_boot_project_api.config.UploadConfig.UPLOAD_DIR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.attachment.AttachmentRequestDTO;
import com.example.spring_boot_project_api.dto.response.attachment.AttachmentResponseDTO;
import com.example.spring_boot_project_api.enums.DocumentTypeEnum;
import com.example.spring_boot_project_api.model.Attachment;
import com.example.spring_boot_project_api.repository.AttachmentRepository;
import com.example.spring_boot_project_api.service.AttachmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
  private final AttachmentRepository attachmentRepository;

  @Override
  public AttachmentResponseDTO createAttachment(AttachmentRequestDTO dto) {
    Attachment attachment = new Attachment();

    attachment.setFileUrl(dto.getFileUrl());
    attachment.setDocumentType(dto.getDocumentType());
    attachment.setIsPrimary(dto.getIsPrimary());
    attachment.setDisplayOrder(dto.getDisplayOrder());

    Attachment saved = attachmentRepository.save(attachment);
    return toResponse(saved);
  }

  @Override
  public AttachmentResponseDTO uploadAttachment(MultipartFile file, DocumentTypeEnum documentType) {
    if (file.isEmpty()) {
      throw new RuntimeException("No file selected");
    }

    String original = file.getOriginalFilename();
    String ext = original != null && original.contains(".")
        ? original.substring(original.lastIndexOf('.') + 1).toLowerCase()
        : "";
    if (!ALLOWED_EXTENSIONS.contains(ext)) {
      throw new RuntimeException("Only jpg, jpeg, png, webp, pdf are allowed");
    }

    try {
      Path dir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
      Files.createDirectories(dir);
      String filename = "attachment-" + System.currentTimeMillis() + "." + ext;
      file.transferTo(dir.resolve(filename).toFile());

      Attachment attachment = new Attachment();
      attachment.setFileUrl("/uploads/" + filename);
      attachment.setDocumentType(documentType);

      Attachment saved = attachmentRepository.save(attachment);
      return toResponse(saved);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save file: " + e.getMessage());
    }
  }

  @Override
  public AttachmentResponseDTO getAttachmentById(Long id) {
    Attachment attachment = attachmentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Attachment not found"));

    return toResponse(attachment);
  }

  @Override
  public List<AttachmentResponseDTO> getAllAttachments() {
    return attachmentRepository.findAll().stream()
        .map(this::toResponse).toList();
  }

  @Override
  public AttachmentResponseDTO updateAttachment(Long id, AttachmentRequestDTO dto) {
    Attachment attachment = attachmentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Attachment not found"));

    attachment.setFileUrl(dto.getFileUrl());
    attachment.setDocumentType(dto.getDocumentType());
    attachment.setIsPrimary(dto.getIsPrimary());
    attachment.setDisplayOrder(dto.getDisplayOrder());

    Attachment saved = attachmentRepository.save(attachment);
    return toResponse(saved);
  }

  @Override
  public void deleteAttachment(Long id) {
    if (!attachmentRepository.existsById(id)) {
      throw new RuntimeException("Attachment not found");
    }

    attachmentRepository.deleteById(id);
  }

  private AttachmentResponseDTO toResponse(Attachment a) {
    return new AttachmentResponseDTO(a.getId(), a.getFileUrl(), a.getDocumentType(), a.getIsPrimary(),
        a.getDisplayOrder(), a.getUploadedAt());
  }
}
