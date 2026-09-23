package com.example.spring_boot_project_api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Singleton settings row (always id = 1) holding the branding/contact info
 * shown across the admin dashboard sidebar/header, the public site
 * header/footer, and the login page. Read via GET /api/settings (public,
 * no auth needed since it must render before login), updated via
 * PUT /api/settings (ADMIN/MANAGER only).
 */
@Entity
@Table(name = "tb_site_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteSettings {

  @Id
  private Long id; // always 1 — enforced in SiteSettingsServiceImpl, not auto-generated

  @NotBlank
  @Column(name = "site_name", nullable = false, length = 100)
  private String siteName;

  @Column(name = "logo_url", length = 500)
  private String logoUrl;

  @Column(name = "favicon_url", length = 500)
  private String faviconUrl;

  @Column(name = "contact_email", length = 100)
  private String contactEmail;

  @Column(name = "contact_phone", length = 30)
  private String contactPhone;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "facebook_url", length = 255)
  private String facebookUrl;

  @Column(name = "telegram_url", length = 255)
  private String telegramUrl;

  @Column(name = "instagram_url", length = 255)
  private String instagramUrl;

  @Column(name = "tiktok_url", length = 255)
  private String tiktokUrl;

  @Column(name = "whatsapp_url", length = 255)
  private String whatsappUrl;

  @Column(name = "linkedin_url", length = 255)
  private String linkedinUrl;

  @Column(name = "website_url", length = 255)
  private String websiteUrl;

  // Photo shown on the left side of the Login/Register pages (uploaded by admin)
  @Column(name = "auth_background_url", length = 500)
  private String authBackgroundUrl;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

}