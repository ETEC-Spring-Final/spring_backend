package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.spring_boot_project_api.model.User;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

  private final Long id;

  public CustomUserDetails(User user) {
    super(user.getEmail(), user.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    this.id = user.getId();
  }

  public Long getId() {
    return id;
  }
}