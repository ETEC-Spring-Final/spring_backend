package com.example.spring_boot_project_api.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.spring_boot_project_api.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expiration;

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(User user) {
    return Jwts.builder()
        .subject(user.getEmail())
        .claim("id", user.getId())
        .claim("role", user.getRole().name())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  public String getEmailFromToken(String token) {
    return getClaims(token).getSubject();
  }

  public String getRoleFromToken(String token) {
    return getClaims(token).get("role", String.class);
  }

  public boolean isValid(String token) {
    try {
      getClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith((javax.crypto.SecretKey) getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}