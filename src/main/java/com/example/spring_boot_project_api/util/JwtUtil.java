package com.example.spring_boot_project_api.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.spring_boot_project_api.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  // Added default fallback values using the : operator in case properties are
  // missing
  @Value("${jwt.secret:${JWT_SECRET:myVerySecretKeyThatIsAtLeast32BytesLongForHS256Algorithm!}}")
  private String secret;

  @Value("${jwt.expiration:${JWT_EXPIRE:86400000}}")
  private long expiration;

  // Use explicit UTF_8 encoding and cast directly to SecretKey
  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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