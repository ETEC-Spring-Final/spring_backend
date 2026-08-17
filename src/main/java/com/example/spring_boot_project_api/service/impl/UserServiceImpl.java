package com.example.spring_boot_project_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.user.LoginRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.RegisterRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.AuthResponseDTO;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.UserService;
import com.example.spring_boot_project_api.util.JwtUtil;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public AuthResponseDTO register(RegisterRequestDTO dto) {
    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new RuntimeException("Email already in use");
    }

    User user = new User();
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setPhone(dto.getPhone());
    user.setGender(dto.getGender());

    if (userRepository.count() == 0) {
      user.setRole(RoleEnum.ADMIN);
    }

    User saved = userRepository.save(user);

    String token = jwtUtil.generateToken(saved);

    return new AuthResponseDTO(saved.getId(), saved.getEmail(), saved.getRole().name(), token);
  }

  @Override
  public AuthResponseDTO login(LoginRequestDTO dto) {
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new RuntimeException("Invalid email or password"));

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid email or password");
    }

    String token = jwtUtil.generateToken(user);
    return new AuthResponseDTO(user.getId(), user.getEmail(), user.getRole().name(), token);
  }
}
