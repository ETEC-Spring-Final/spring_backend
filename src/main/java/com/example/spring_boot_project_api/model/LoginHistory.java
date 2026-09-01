package com.example.spring_boot_project_api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_login_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "attempted_username")
  private String attemptedUsername;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "device", length = 500)
  private String device;

  @Column(name = "success", nullable = false)
  private Boolean success;

  @CreationTimestamp
  @Column(name = "logged_in_at", nullable = false, updatable = false)
  private LocalDateTime loggedInAt;

  @Column(name = "logged_out_at")
  private LocalDateTime loggedOutAt;
}