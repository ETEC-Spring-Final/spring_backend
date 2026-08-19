package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
  // duplication check
  boolean existsByUserIdAndVehicleId(Long userId, Long vehicleId);

  // list a user's favorite
  List<Favorite> findByUserId(Long userId);

  // unfavorite
  void deleteByUserIdAndVehicleId(Long userId, Long vehicleId);
}
