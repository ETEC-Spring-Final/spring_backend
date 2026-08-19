package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.response.favorite.FavoriteResponseDTO;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
  @Autowired
  private FavoriteService favoriteService;

  @Autowired
  private UserRepository userRepository;

  @PostMapping("/{vehicleId}")
  public FavoriteResponseDTO addFavorite(@PathVariable Long vehicleId, Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));
    return favoriteService.addFavorite(user.getId(), vehicleId);
  }

  @GetMapping
  public List<FavoriteResponseDTO> getFavorites(Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));
    return favoriteService.getFavorites(user.getId());
  }

  @DeleteMapping("/{vehicleId}")
  public void removeFavorite(@PathVariable Long vehicleId, Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));
    favoriteService.removeFavorite(user.getId(), vehicleId);
  }
}
