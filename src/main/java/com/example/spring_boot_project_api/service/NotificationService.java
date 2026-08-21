package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.response.notification.NotificationResponseDTO;
import com.example.spring_boot_project_api.enums.NotificationTypeEnum;

public interface NotificationService {
  void createNotification(Long userId, NotificationTypeEnum type, String title, String message);

  List<NotificationResponseDTO> getNotificationsForUser(Long userId);
}
