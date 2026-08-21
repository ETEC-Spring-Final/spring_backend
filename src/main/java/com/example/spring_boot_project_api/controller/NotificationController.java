package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.notification.NotificationRequestDTO;
import com.example.spring_boot_project_api.dto.response.notification.NotificationResponseDTO;
import com.example.spring_boot_project_api.service.NotificationService;
import com.example.spring_boot_project_api.service.impl.CustomUserDetails;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
  @Autowired
  private NotificationService notificationService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping("/{userId}/notify")
  public NotificationResponseDTO createNotification(@PathVariable Long userId,
      @Valid @RequestBody NotificationRequestDTO dto) {
    return notificationService.createNotification(userId, dto);
  }

  @GetMapping("/me/inbox")
  public List<NotificationResponseDTO> getNotificationsForUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
    return notificationService.getNotificationsForUser(userDetails.getId());
  }
}
