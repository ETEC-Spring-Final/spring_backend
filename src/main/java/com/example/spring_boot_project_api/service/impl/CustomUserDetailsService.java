package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
        .build();
  }
}