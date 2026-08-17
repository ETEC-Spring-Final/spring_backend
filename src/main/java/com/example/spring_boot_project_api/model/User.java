package com.example.spring_boot_project_api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.spring_boot_project_api.enums.GenderEnum;
import com.example.spring_boot_project_api.enums.RoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@jakarta.persistence.Table(name = "tb_users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(name = "first_name", nullable = false, length = 50)
  @Size(max = 50, message = "First name must be under 50 characters")
  private String firstName;

  @NotBlank
  @Column(name = "last_name", nullable = false, length = 50)
  @Size(max = 50, message = "Last name must be under 50 characters")
  private String lastName;

  @NotBlank
  @Column(name = "email", nullable = false, unique = true, length = 50)
  @Email(message = "Email must be valid")
  @Size(max = 50, message = "Email must be under 50 characters")
  private String email;

  @NotBlank
  @Column(name = "password", nullable = false, length = 100)
  @Size(min = 8, max = 100, message = "Password must be between 8-100 characters")
  private String password;

  @NotBlank
  @Column(name = "phone", nullable = false, length = 10)
  @Size(min = 9, max = 10, message = "Phone number is only 9-10 digits")
  private String phone;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false)
  private GenderEnum gender;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private RoleEnum role = RoleEnum.CUSTOMER;

  @Column(name = "profile_picture", length = 255)
  private String profilePicture;

  @Column(name = "active")
  private Boolean active = true;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
