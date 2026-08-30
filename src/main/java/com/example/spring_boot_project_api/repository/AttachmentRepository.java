package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.enums.DocumentTypeEnum;
import com.example.spring_boot_project_api.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
  List<Attachment> findByDocumentType(DocumentTypeEnum documentType);
}
