package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.attachment.AttachmentRequestDTO;
import com.example.spring_boot_project_api.dto.response.attachment.AttachmentResponseDTO;
import com.example.spring_boot_project_api.enums.DocumentTypeEnum;
import com.example.spring_boot_project_api.service.AttachmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {
  @Autowired
  private AttachmentService attachmentService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping
  public AttachmentResponseDTO createAttachment(@Valid @RequestBody AttachmentRequestDTO dto) {
    return attachmentService.createAttachment(dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public AttachmentResponseDTO upload(@RequestParam("file") MultipartFile file,
      @RequestParam(required = false) DocumentTypeEnum documentType) {
    return attachmentService.uploadAttachment(file, documentType);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping("/{id}")
  public AttachmentResponseDTO getAttachmentById(@PathVariable Long id) {
    return attachmentService.getAttachmentById(id);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping
  public List<AttachmentResponseDTO> getAllAttachments() {
    return attachmentService.getAllAttachments();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public AttachmentResponseDTO updateAttachment(@PathVariable Long id, @Valid @RequestBody AttachmentRequestDTO dto) {
    return attachmentService.updateAttachment(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @DeleteMapping("/{id}")
  public void deleteAttachment(@PathVariable Long id) {
    attachmentService.deleteAttachment(id);
  }
}
