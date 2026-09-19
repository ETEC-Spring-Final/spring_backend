package com.example.spring_boot_project_api.service.impl;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.spring_boot_project_api.dto.response.cloudinary.CloudinaryUploadResponseDTO;
import com.example.spring_boot_project_api.service.CloudinaryUploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadServiceImpl implements CloudinaryUploadService {

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
  private static final Set<String> ALLOWED_FOLDERS = Set.of(
      "vehicle-images", "profile-pictures", "site-settings", "avatars", "admin-profiles", "misc");
  private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
  private static final String DEFAULT_FOLDER = "misc";

  private final Cloudinary cloudinary;

  @Override
  public CloudinaryUploadResponseDTO upload(MultipartFile file, String folder) {
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file selected");
    }

    if (file.getSize() > MAX_FILE_SIZE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File too large: max size is 5MB");
    }

    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Only jpeg, png and webp images are allowed");
    }

    String original = file.getOriginalFilename();
    String ext = original != null && original.contains(".")
        ? original.substring(original.lastIndexOf('.') + 1).toLowerCase()
        : "";
    if (!ALLOWED_EXTENSIONS.contains(ext)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Only jpg, jpeg, png, webp are allowed");
    }

    String targetFolder = folder == null || folder.isBlank() ? DEFAULT_FOLDER : folder.trim();
    if (!ALLOWED_FOLDERS.contains(targetFolder)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid folder: must be one of " + ALLOWED_FOLDERS);
    }

    try {
      Map<?, ?> result = cloudinary.uploader().upload(
          file.getBytes(), ObjectUtils.asMap("folder", targetFolder));

      String url = String.valueOf(result.get("secure_url"));
      String publicId = String.valueOf(result.get("public_id"));
      return new CloudinaryUploadResponseDTO(url, publicId, Instant.now().toString());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Cloudinary upload failed: " + e.getMessage());
    }
  }

  @Override
  public void delete(String publicId) {
    if (publicId == null || publicId.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing public_id");
    }

    boolean allowedFolder = ALLOWED_FOLDERS.stream()
        .anyMatch(folder -> publicId.startsWith(folder + "/"));
    if (!allowedFolder) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid asset: public_id must be inside one of " + ALLOWED_FOLDERS);
    }

    try {
      Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
      Object status = result.get("result");
      if ("not found".equalsIgnoreCase(String.valueOf(status))) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found: " + publicId);
      }
      if (!"ok".equals(String.valueOf(status))) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Failed to delete asset: " + String.valueOf(status));
      }
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Cloudinary delete failed: " + e.getMessage());
    }
  }
}