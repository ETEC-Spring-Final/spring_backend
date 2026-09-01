package com.example.spring_boot_project_api.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.spring_boot_project_api.dto.request.user.LoginRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.RegisterRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.AuthResponseDTO;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.LoginHistoryService;
import com.example.spring_boot_project_api.service.UserService;
import com.example.spring_boot_project_api.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final LoginHistoryService loginHistoryService;

  @Override
  @Transactional
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
  @Transactional
  public AuthResponseDTO login(LoginRequestDTO dto) {
    HttpServletRequest request = getCurrentHttpRequest();
    String ipAddress = getClientIp(request);
    String device = getUserAgent(request);

    User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

    if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      loginHistoryService.recordLoginAttempt(dto.getEmail(), false, ipAddress, device);
      throw new RuntimeException("Invalid email or password");
    }

    loginHistoryService.recordLoginAttempt(user.getEmail(), true, ipAddress, device);

    String token = jwtUtil.generateToken(user);
    return new AuthResponseDTO(user.getId(), user.getEmail(), user.getRole().name(), token);
  }

  private HttpServletRequest getCurrentHttpRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attributes != null ? attributes.getRequest() : null;
  }

  private String getClientIp(HttpServletRequest request) {
    if (request == null)
      return "UNKNOWN";
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isBlank()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private String getUserAgent(HttpServletRequest request) {
    if (request == null)
      return "UNKNOWN";
    String userAgent = request.getHeader("User-Agent");
    return userAgent != null ? userAgent : "UNKNOWN";
  }
}