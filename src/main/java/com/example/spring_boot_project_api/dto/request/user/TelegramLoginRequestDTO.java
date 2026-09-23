package com.example.spring_boot_project_api.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payload posted by the Telegram Login Widget's data-onauth callback.
 * Telegram sends snake_case field names (id, first_name, auth_date, ...);
 * @JsonProperty maps them onto normal camelCase Java fields so this DTO
 * doesn't fight the rest of the codebase's naming convention.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramLoginRequestDTO {

  @NotNull
  private Long id;

  @NotBlank
  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  private String username;

  @JsonProperty("photo_url")
  private String photoUrl;

  @NotNull
  @JsonProperty("auth_date")
  private Long authDate;

  @NotBlank
  private String hash;
}