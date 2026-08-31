package com.example.spring_boot_project_api.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.attachment.AttachmentRequestDTO;
import com.example.spring_boot_project_api.dto.response.attachment.AttachmentResponseDTO;
import com.example.spring_boot_project_api.enums.DocumentTypeEnum;

public interface AttachmentService {
  AttachmentResponseDTO createAttachment(AttachmentRequestDTO dto);

  AttachmentResponseDTO uploadAttachment(MultipartFile file, DocumentTypeEnum documentType);

  AttachmentResponseDTO getAttachmentById(Long id);

  List<AttachmentResponseDTO> getAllAttachments();

  AttachmentResponseDTO updateAttachment(Long id, AttachmentRequestDTO dto);

  void deleteAttachment(Long id);
}
