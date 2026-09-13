package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.notification.NotificationRequestDTO;
import com.example.spring_boot_project_api.dto.response.notification.NotificationResponseDTO;

public interface NotificationService {
  NotificationResponseDTO createNotification(Long userId, NotificationRequestDTO dto);

  List<NotificationResponseDTO> getNotificationsForUser(Long userId);

  List<NotificationResponseDTO> getAllNotifications();

  // Mark a single notification as read (owner or staff only)
  NotificationResponseDTO markAsRead(Long id, Long requestingUserId);

  // Mark every notification belonging to this user as read
  void markAllAsRead(Long userId);

  // For the unread-count badge
  long getUnreadCount(Long userId);

  // Delete a notification (owner or staff only)
  void deleteNotification(Long id, Long requestingUserId);
}