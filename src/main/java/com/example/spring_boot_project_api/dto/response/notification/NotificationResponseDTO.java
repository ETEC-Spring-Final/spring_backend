package com.example.spring_boot_project_api.dto.response.notification;

import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.NotificationTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponseDTO {
  private Long id;
  private NotificationTypeEnum type;
  private String title;
  private String message;
  private Boolean isRead;
  private LocalDateTime createdAt;
}
