package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  // Newest notifications first
  List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

  // For the unread-count badge
  long countByUserIdAndIsReadFalse(Long userId);
}