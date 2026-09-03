package com.example.spring_boot_project_api.dto.response.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryResponseDTO {
  private Long id;
  private String attemptedUsername;
  private String ipAddress;
  private String device;
  private Boolean success;
  private LocalDateTime loggedInAt;
  private LocalDateTime loggedOutAt;
}