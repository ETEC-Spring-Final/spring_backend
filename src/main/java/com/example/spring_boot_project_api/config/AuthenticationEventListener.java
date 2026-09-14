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
    // LOCAL (email/password) login: principal is a Spring Security UserDetails
    // (e.g. CustomUserDetails) — getUsername() returns the email we set as
    // the "username" in CustomUserDetails' constructor.
    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    }
    // OAuth2 (Google/Facebook) login: principal is our CustomOAuth2User,
    // which does NOT implement UserDetails and has no toString() override,
    // so falling through to principal.toString() previously produced
    // "com.example...CustomOAuth2User@<hashcode>" instead of the email.
    // Read the real User entity we attached in CustomOAuth2UserService.
    if (principal instanceof CustomOAuth2User oAuth2User) {
      return oAuth2User.getUser().getEmail();
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