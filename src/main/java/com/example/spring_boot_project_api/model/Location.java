package com.example.spring_boot_project_api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(name = "name", nullable = false, length = 100)
  @Size(max = 100, message = "Garage name cannot exceed 100 characters")
  private String name;

  @NotBlank
  @Column(name = "address", nullable = false, length = 255)
  @Size(max = 255, message = "Address cannot exceed 255 characters")
  private String address;

  @NotBlank
  @Column(name = "city", nullable = false, length = 100)
  @Size(max = 100, message = "Shop name cannot exceed 100 characters")
  private String city;

  @NotBlank
  @Column(name = "phone", nullable = false, length = 10)
  @Size(min = 9, max = 10, message = "Phone number is only 9-10 digits")
  private String phone;

  @Column(name = "is_active")
  private Boolean isActive;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
