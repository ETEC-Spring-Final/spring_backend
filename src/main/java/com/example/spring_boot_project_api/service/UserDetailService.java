package com.example.spring_boot_project_api.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailService {
  UserDetails loadUserDetails(String username);
}
