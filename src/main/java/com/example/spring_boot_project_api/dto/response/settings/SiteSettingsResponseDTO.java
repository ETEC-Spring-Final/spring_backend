package com.example.spring_boot_project_api.dto.response.settings;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SiteSettingsResponseDTO {
  private Long id;
  private String siteName;
  private String logoUrl;
  private String faviconUrl;
  private String contactEmail;
  private String contactPhone;
  private String address;
  private String facebookUrl;
  private String telegramUrl;
  private String instagramUrl;
  private String tiktokUrl;
  private String whatsappUrl;
  private String linkedinUrl;
  private String websiteUrl;
  private String authBackgroundUrl;
  private LocalDateTime updatedAt;
}