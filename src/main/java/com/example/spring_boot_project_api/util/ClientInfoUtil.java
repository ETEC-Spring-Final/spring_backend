package com.example.spring_boot_project_api.util;

import jakarta.servlet.http.HttpServletRequest;

public class ClientInfoUtil {

  public static String getClientIp(HttpServletRequest request) {
    if (request == null)
      return "UNKNOWN";

    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }

    // X-Forwarded-For can contain multiple IPs separated by commas; pick the client
    // IP
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }

    return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
  }

  public static String getUserAgent(HttpServletRequest request) {
    if (request == null)
      return "UNKNOWN";
    String userAgent = request.getHeader("User-Agent");
    return userAgent != null ? userAgent : "UNKNOWN";
  }
}