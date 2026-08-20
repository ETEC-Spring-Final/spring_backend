package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.location.LocationRequestDTO;
import com.example.spring_boot_project_api.dto.response.location.LocationResponseDTO;

public interface LocationService {
  LocationResponseDTO createLocation(LocationRequestDTO dto);

  LocationResponseDTO getLocationById(Long id);

  List<LocationResponseDTO> getAllLocations();

  LocationResponseDTO updateLocation(Long id, LocationRequestDTO dto);

  void deleteLocation(Long id);
}
