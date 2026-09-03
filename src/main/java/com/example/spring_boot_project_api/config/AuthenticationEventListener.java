package com.example.spring_boot_project_api.config;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.spring_boot_project_api.service.LoginHistoryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

  private final LoginHistoryService loginHistoryService;

  @EventListener
  public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
    String username = extractUsername(event.getAuthentication().getPrincipal());
    HttpServletRequest request = getCurrentHttpRequest();

    String ipAddress = getClientIp(request);
    String device = getUserAgent(request);

    loginHistoryService.recordLoginAttempt(username, true, ipAddress, device);
  }

  @EventListener
  public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
    String username = event.getAuthentication().getName();
    HttpServletRequest request = getCurrentHttpRequest();

    String ipAddress = getClientIp(request);
    String device = getUserAgent(request);

    loginHistoryService.recordLoginAttempt(username, false, ipAddress, device);
  }

  private String extractUsername(Object principal) {
    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    }
    return principal.toString();
  }

  private HttpServletRequest getCurrentHttpRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attributes != null ? attributes.getRequest() : null;
  }

  private String getClientIp(HttpServletRequest request) {
    if (request == null) {
      return "UNKNOWN";
    }

    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isBlank()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private String getUserAgent(HttpServletRequest request) {
    if (request == null) {
      return "UNKNOWN";
    }

    String userAgent = request.getHeader("User-Agent");
    return userAgent != null ? userAgent : "UNKNOWN";
  }
}