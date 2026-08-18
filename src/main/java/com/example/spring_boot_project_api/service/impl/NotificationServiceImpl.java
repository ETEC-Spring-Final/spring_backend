package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.response.notification.NotificationResponse;
import com.example.spring_boot_project_api.enums.NotificationTypeEnum;
import com.example.spring_boot_project_api.model.Notification;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.NotificationRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Override
  public void createNotification(Long userId, NotificationTypeEnum type, String title, String message) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    Notification notification = new Notification();
    notification.setUser(user);
    notification.setType(type);
    notification.setTitle(title);
    notification.setMessage(message);
    notification.setIsRead(false);

    notificationRepository.save(notification);
  }

  @Override
  public List<NotificationResponse> getNotificationForUser(Long userId) {
    return notificationRepository.findById(userId).stream()
        .map(n -> new NotificationResponse(n.getId(), n.getType(),
            n.getTitle(), n.getMessage(), n.getIsRead(), n.getCreatedAt()))
        .toList();
  }
}
