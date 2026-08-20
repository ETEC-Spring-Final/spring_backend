package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.location.LocationRequestDTO;
import com.example.spring_boot_project_api.dto.response.location.LocationResponseDTO;
import com.example.spring_boot_project_api.model.Location;
import com.example.spring_boot_project_api.repository.LocationRepository;
import com.example.spring_boot_project_api.service.LocationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
  private final LocationRepository locationRepository;

  @Override
  public LocationResponseDTO createLocation(LocationRequestDTO dto) {
    if (locationRepository.findByAddress(dto.getAddress()).isPresent()) {
      throw new RuntimeException("A location with that address already exists");
    }

    Location location = new Location();
    location.setName(dto.getName());
    location.setAddress(dto.getAddress());
    location.setCity(dto.getCity());
    location.setPhone(dto.getPhone());
    location.setIsActive(dto.getIsActive());

    Location saved = locationRepository.save(location);
    return toResponse(saved);
  }

  @Override
  public LocationResponseDTO getLocationById(Long id) {
    Location location = locationRepository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));

    return toResponse(location);
  }

  @Override
  public List<LocationResponseDTO> getAllLocations() {
    return locationRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public LocationResponseDTO updateLocation(Long id, LocationRequestDTO dto) {
    locationRepository.findByAddress(dto.getAddress()).filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> {
          throw new RuntimeException("A location with that address already exists");
        });

    Location location = new Location();
    location.setName(dto.getName());
    location.setAddress(dto.getAddress());
    location.setCity(dto.getCity());
    location.setPhone(dto.getPhone());
    location.setIsActive(dto.getIsActive());

    Location saved = locationRepository.save(location);
    return toResponse(saved);
  }

  @Override
  public void deleteLocation(Long id) {
    if (!locationRepository.existsById(id)) {
      throw new RuntimeException("Location not found");
    }
    locationRepository.deleteById(id);
  }

  private LocationResponseDTO toResponse(Location l) {
    return new LocationResponseDTO(l.getId(), l.getName(), l.getAddress(), l.getCity(), l.getPhone(), l.getIsActive(),
        l.getCreatedAt());
  }
}
