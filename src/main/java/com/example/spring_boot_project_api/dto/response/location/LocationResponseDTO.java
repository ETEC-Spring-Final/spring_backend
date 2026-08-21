package com.example.spring_boot_project_api.dto.response.location;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationResponseDTO {
  private Long id;
  private String name;
  private String address;
  private String city;
  private String phone;
  private Boolean isActive;
  private LocalDateTime createdAt;
}
