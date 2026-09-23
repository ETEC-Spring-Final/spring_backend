package com.example.spring_boot_project_api.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.user.TelegramLoginRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.AuthResponseDTO;
import com.example.spring_boot_project_api.enums.AuthProviderEnum;
import com.example.spring_boot_project_api.enums.GenderEnum;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.util.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * Verifies the Telegram Login Widget payload (per Telegram's official
 * spec: https://core.telegram.org/widgets/login#checking-authorization),
 * then finds-or-creates a User the same way CustomOAuth2UserService does
 * for Google/Facebook.
 *
 * Telegram never gives us an email, so we synthesize a stable placeholder
 * (telegram_<id>@telegram.local) purely to satisfy the NOT NULL/unique
 * email column — it is never used to contact the user or matched against
 * a LOCAL account by email (unlike Google/Facebook linking).
 */
@Service
@RequiredArgsConstructor
public class TelegramAuthService {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  @Value("${telegram.bot.token}")
  private String botToken;

  // Reject a stale/replayed widget payload even if its hash is still valid.
  private static final long MAX_AUTH_AGE_SECONDS = 86_400; // 24h

  public AuthResponseDTO loginOrRegister(TelegramLoginRequestDTO dto) {
    verifyHash(dto);
    verifyFreshness(dto);

    String providerId = String.valueOf(dto.getId());
    String syntheticEmail = "telegram_" + dto.getId() + "@telegram.local";

    User user = userRepository.findByProviderIdAndAuthProvider(providerId, AuthProviderEnum.TELEGRAM)
        .orElseGet(() -> {
          User created = User.builder()
              .firstName(blankToUser(dto.getFirstName()))
              .lastName(dto.getLastName() == null ? "" : dto.getLastName())
              .email(syntheticEmail)
              .password(UUID.randomUUID().toString()) // unusable — never logged in with a password
              .phone("0000000000") // placeholder — prompt user to complete profile afterward
              .gender(GenderEnum.MALE) // placeholder — prompt user to complete profile afterward
              .profilePicture(dto.getPhotoUrl())
              .role(RoleEnum.CUSTOMER)
              .authProvider(AuthProviderEnum.TELEGRAM)
              .providerId(providerId)
              .active(true)
              .build();
          return userRepository.save(created);
        });

    String token = jwtUtil.generateToken(user);

    // AuthResponseDTO only has @Data + @AllArgsConstructor (no @Builder),
    // so it's constructed positionally here to match its declared field
    // order: (id, email, role, token).
    return new AuthResponseDTO(user.getId(), user.getEmail(), user.getRole(), token);
  }

  private String blankToUser(String value) {
    return (value == null || value.isBlank()) ? "User" : value;
  }

  /**
   * Recomputes HMAC-SHA256(data_check_string, SHA256(bot_token)) and
   * compares it against the hash the widget sent, using a constant-time
   * comparison so a timing attack can't be used to guess it byte-by-byte.
   */
  private void verifyHash(TelegramLoginRequestDTO dto) {
    Map<String, String> fields = new TreeMap<>();
    fields.put("id", String.valueOf(dto.getId()));
    fields.put("first_name", dto.getFirstName());
    if (dto.getLastName() != null) fields.put("last_name", dto.getLastName());
    if (dto.getUsername() != null) fields.put("username", dto.getUsername());
    if (dto.getPhotoUrl() != null) fields.put("photo_url", dto.getPhotoUrl());
    fields.put("auth_date", String.valueOf(dto.getAuthDate()));

    StringBuilder dataCheckString = new StringBuilder();
    for (Map.Entry<String, String> entry : fields.entrySet()) {
      if (dataCheckString.length() > 0) dataCheckString.append('\n');
      dataCheckString.append(entry.getKey()).append('=').append(entry.getValue());
    }

    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      byte[] secretKey = sha256.digest(botToken.getBytes(StandardCharsets.UTF_8));

      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
      byte[] computed = mac.doFinal(dataCheckString.toString().getBytes(StandardCharsets.UTF_8));
      String computedHex = bytesToHex(computed);

      boolean valid = MessageDigest.isEqual(
          computedHex.getBytes(StandardCharsets.UTF_8),
          dto.getHash().getBytes(StandardCharsets.UTF_8));

      if (!valid) {
        throw new OAuth2AuthenticationException("Invalid Telegram login signature.");
      }
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalStateException("Failed to verify Telegram login hash", e);
    }
  }

  private void verifyFreshness(TelegramLoginRequestDTO dto) {
    long now = Instant.now().getEpochSecond();
    if (now - dto.getAuthDate() > MAX_AUTH_AGE_SECONDS) {
      throw new OAuth2AuthenticationException("Telegram login payload has expired. Please try again.");
    }
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) sb.append(String.format("%02x", b));
    return sb.toString();
  }
}