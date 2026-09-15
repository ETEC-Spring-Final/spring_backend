package com.example.spring_boot_project_api.service;

import com.example.spring_boot_project_api.dto.request.settings.SiteSettingsRequestDTO;
import com.example.spring_boot_project_api.dto.response.settings.SiteSettingsResponseDTO;

public interface SiteSettingsService {

  // Returns the singleton settings row, creating sensible defaults on
  // first call if the table is empty (e.g. brand-new database).
  SiteSettingsResponseDTO getSettings();

  SiteSettingsResponseDTO updateSettings(SiteSettingsRequestDTO dto);
}