package com.example.spring_boot_project_api.dto.request.notification;

import com.example.spring_boot_project_api.enums.NotificationTypeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {
  @NotNull(message = "Notification type is required")
  private NotificationTypeEnum type;

  @NotBlank(message = "Title is required")
  private String title;

  @NotBlank(message = "Message is required")
  private String message;
}
