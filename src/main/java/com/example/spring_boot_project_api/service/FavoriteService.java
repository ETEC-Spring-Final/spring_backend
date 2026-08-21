package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.response.favorite.FavoriteResponseDTO;

public interface FavoriteService {
  FavoriteResponseDTO addFavorite(Long userId, Long vehicleId);

  List<FavoriteResponseDTO> getFavorites(Long userId);

  void removeFavorite(Long userId, Long vehicleId);
}
