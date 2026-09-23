package com.example.spring_boot_project_api.dto.request.settings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SiteSettingsRequestDTO {

  @NotBlank(message = "Site name is required")
  @Size(max = 100, message = "Site name must be under 100 characters")
  private String siteName;

  @Size(max = 500, message = "Logo URL must be under 500 characters")
  private String logoUrl;

  @Size(max = 500, message = "Favicon URL must be under 500 characters")
  private String faviconUrl;

  @Email(message = "Contact email must be a valid email address")
  @Size(max = 100)
  private String contactEmail;

  @Size(max = 30, message = "Contact phone must be under 30 characters")
  private String contactPhone;

  @Size(max = 255)
  private String address;

  @Size(max = 255)
  private String facebookUrl;

  @Size(max = 255)
  private String telegramUrl;

  @Size(max = 255, message = "Instagram URL must be under 255 characters")
  private String instagramUrl;

  @Size(max = 255, message = "TikTok URL must be under 255 characters")
  private String tiktokUrl;

  @Size(max = 255, message = "WhatsApp URL must be under 255 characters")
  private String whatsappUrl;

  @Size(max = 255, message = "LinkedIn URL must be under 255 characters")
  private String linkedinUrl;

  @Size(max = 255, message = "Website URL must be under 255 characters")
  private String websiteUrl;

  @Size(max = 500, message = "Auth background URL must be under 500 characters")
  private String authBackgroundUrl;
}