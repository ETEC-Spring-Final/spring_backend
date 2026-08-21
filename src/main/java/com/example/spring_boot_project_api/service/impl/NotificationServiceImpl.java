package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.notification.NotificationRequestDTO;
import com.example.spring_boot_project_api.dto.response.notification.NotificationResponse;
import com.example.spring_boot_project_api.dto.response.notification.NotificationResponseDTO;
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
  public void createNotification(NotificationRequestDTO dto) {
    User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

    Notification notification = new Notification();
    notification.setUser(user);
    notification.setType(dto.getType());
    notification.setTitle(dto.getTitle());
    notification.setMessage(dto.getMessage());

    notification saved = notificationRepository.save(notification);
    return toResponse(saved);
  }

  @Override
  public List<NotificationResponseDTO> getNotificationsForUser(Long userId) {
    return notificationRepository.findByUserId(userId).stream()
        .map(this::toResponse)
        .toList();
  }

  private NotificationResponseDTO toResponse(Notification n) {
    return new NotificationResponseDTO(n.getId(), n.getType(), n.getTitle(), n.getMessage(), n.getIsRead(),
        n.getCreatedAt());
  }
}
