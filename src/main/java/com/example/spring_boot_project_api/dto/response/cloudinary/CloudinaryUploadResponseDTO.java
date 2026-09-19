package com.example.spring_boot_project_api.dto.response.cloudinary;

public record CloudinaryUploadResponseDTO(String url, String publicId, String uploadedAt) {
}