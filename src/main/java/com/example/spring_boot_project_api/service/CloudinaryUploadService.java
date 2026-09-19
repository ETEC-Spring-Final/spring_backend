package com.example.spring_boot_project_api.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.response.cloudinary.CloudinaryUploadResponseDTO;

public interface CloudinaryUploadService {

  CloudinaryUploadResponseDTO upload(MultipartFile file, String folder);

  void delete(String publicId);
}