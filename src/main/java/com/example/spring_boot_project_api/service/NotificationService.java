package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.notification.NotificationRequestDTO;
import com.example.spring_boot_project_api.dto.response.notification.NotificationResponseDTO;

public interface NotificationService {
  NotificationResponseDTO createNotification(Long userId, NotificationRequestDTO dto);

  List<NotificationResponseDTO> getNotificationsForUser(Long userId);
}
