package com.example.spring_boot_project_api.service.impl;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.settings.SiteSettingsRequestDTO;
import com.example.spring_boot_project_api.dto.response.settings.SiteSettingsResponseDTO;
import com.example.spring_boot_project_api.model.SiteSettings;
import com.example.spring_boot_project_api.repository.SiteSettingsRepository;
import com.example.spring_boot_project_api.service.SiteSettingsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteSettingsServiceImpl implements SiteSettingsService {

  private static final Long SINGLETON_ID = 1L;

  private final SiteSettingsRepository siteSettingsRepository;

  @Override
  public SiteSettingsResponseDTO getSettings() {
    SiteSettings settings = siteSettingsRepository.findById(SINGLETON_ID)
        .orElseGet(this::createDefaults);
    return toResponse(settings);
  }

  @Override
  public SiteSettingsResponseDTO updateSettings(SiteSettingsRequestDTO dto) {
    SiteSettings settings = siteSettingsRepository.findById(SINGLETON_ID)
        .orElseGet(this::createDefaults);

    settings.setSiteName(dto.getSiteName());
    settings.setLogoUrl(dto.getLogoUrl());
    settings.setFaviconUrl(dto.getFaviconUrl());
    settings.setAuthBackgroundUrl(dto.getAuthBackgroundUrl());
    settings.setContactEmail(dto.getContactEmail());
    settings.setContactPhone(dto.getContactPhone());
    settings.setAddress(dto.getAddress());
    settings.setFacebookUrl(dto.getFacebookUrl());
    settings.setTelegramUrl(dto.getTelegramUrl());
    settings.setInstagramUrl(dto.getInstagramUrl());
    settings.setTiktokUrl(dto.getTiktokUrl());
    settings.setWhatsappUrl(dto.getWhatsappUrl());
    settings.setLinkedinUrl(dto.getLinkedinUrl());
    settings.setWebsiteUrl(dto.getWebsiteUrl());

    SiteSettings saved = siteSettingsRepository.save(settings);
    return toResponse(saved);
  }

  // Called only the very first time GET/PUT /api/settings is hit on a
  // fresh database — persists a default row with id=1 so every later
  // read/update just updates this one row instead of creating duplicates.
  private SiteSettings createDefaults() {
    SiteSettings defaults = SiteSettings.builder()
        .id(SINGLETON_ID)
        .siteName("CarRental Admin")
        .logoUrl(null)
        .faviconUrl(null)
        .authBackgroundUrl(null)
        .contactEmail(null)
        .contactPhone(null)
        .address(null)
        .facebookUrl(null)
        .telegramUrl(null)
        .instagramUrl(null)
        .tiktokUrl(null)
        .whatsappUrl(null)
        .linkedinUrl(null)
        .websiteUrl(null)
        .build();
    return siteSettingsRepository.save(defaults);
  }

  // ⚠️ Order here MUST match SiteSettingsResponseDTO's @AllArgsConstructor
  // field order exactly, or values silently shift into the wrong field.
  private SiteSettingsResponseDTO toResponse(SiteSettings s) {
    return new SiteSettingsResponseDTO(
        s.getId(), s.getSiteName(), s.getLogoUrl(), s.getFaviconUrl(),
        s.getContactEmail(), s.getContactPhone(), s.getAddress(),
        s.getFacebookUrl(), s.getTelegramUrl(),
        s.getInstagramUrl(), s.getTiktokUrl(), s.getWhatsappUrl(),
        s.getLinkedinUrl(), s.getWebsiteUrl(),
        s.getAuthBackgroundUrl(),
        s.getUpdatedAt());
  }
}