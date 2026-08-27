package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.response.favorite.FavoriteResponseDTO;
import com.example.spring_boot_project_api.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
  @Autowired
  private FavoriteService favoriteService;

  @PostMapping("/{vehicleId}")
  public FavoriteResponseDTO addFavorite(@PathVariable Long vehicleId) {
    return favoriteService.addFavorite(vehicleId);
  }

  @GetMapping
  public List<FavoriteResponseDTO> getFavorites() {
    return favoriteService.getFavorites();
  }

  @DeleteMapping("/{vehicleId}")
  public void removeFavorite(@PathVariable Long vehicleId) {
    favoriteService.removeFavorite(vehicleId);
  }
}
