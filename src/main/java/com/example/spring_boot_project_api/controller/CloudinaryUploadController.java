package com.example.spring_boot_project_api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.response.cloudinary.CloudinaryUploadResponseDTO;
import com.example.spring_boot_project_api.service.CloudinaryUploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class CloudinaryUploadController {

  private final CloudinaryUploadService cloudinaryUploadService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CloudinaryUploadResponseDTO upload(@RequestParam("file") MultipartFile file,
      @RequestParam(value = "folder", required = false, defaultValue = "misc") String folder) {
    return cloudinaryUploadService.upload(file, folder);
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(@RequestParam("publicId") String publicId) {
    cloudinaryUploadService.delete(publicId);
    return ResponseEntity.noContent().build();
  }
}