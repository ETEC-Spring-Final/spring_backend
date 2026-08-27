package com.example.spring_boot_project_api.config;

import java.util.Set;

public class UploadConfig {
  public static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
  public static final String UPLOAD_DIR = "uploads";
}