package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.response.favorite.FavoriteResponseDTO;
import com.example.spring_boot_project_api.dto.response.vehicle.VehicleResponseDTO;
import com.example.spring_boot_project_api.model.Favorite;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.model.Vehicle;
import com.example.spring_boot_project_api.repository.FavoriteRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.repository.VehicleRepository;
import com.example.spring_boot_project_api.service.FavoriteService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;
  private final VehicleRepository vehicleRepository;

  @Override
  public FavoriteResponseDTO addFavorite(Long userId, Long vehicleId) {
    if (favoriteRepository.existsByUserIdAndVehicleId(userId, vehicleId)) {
      throw new RuntimeException("Vehicle already in favorites");
    }

    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    Vehicle vehicle = vehicleRepository.findById(vehicleId)
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    Favorite favorite = new Favorite();
    favorite.setUser(user);
    favorite.setVehicle(vehicle);

    Favorite saved = favoriteRepository.save(favorite);
    return toResponse(saved);
  }

  @Override
  public List<FavoriteResponseDTO> getFavorites(Long userId) {
    return favoriteRepository.findByUserId(userId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  @Transactional
  public void removeFavorite(Long userId, Long vehicleId) {
    favoriteRepository.deleteByUserIdAndVehicleId(userId, vehicleId);
  }

  private FavoriteResponseDTO toResponse(Favorite f) {
    VehicleResponseDTO vehicleDto = new VehicleResponseDTO(
        f.getVehicle().getId(), f.getVehicle().getBrand(), f.getVehicle().getModel(),
        f.getVehicle().getYearOfManufacture(), f.getVehicle().getLicensePlate(),
        f.getVehicle().getColor(), f.getVehicle().getType(), f.getVehicle().getTransmission(),
        f.getVehicle().getFuelType(), f.getVehicle().getSeats(), f.getVehicle().getPricePerDay(),
        f.getVehicle().getMileAge(), f.getVehicle().getDescription(), f.getVehicle().getStatus(),
        f.getVehicle().getCreatedAt(), f.getVehicle().getUpdatedAt());

    return new FavoriteResponseDTO(f.getId(), vehicleDto, f.getCreatedAt());
  }
}
