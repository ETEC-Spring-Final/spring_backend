package com.example.spring_boot_project_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      CustomOAuth2UserService customOAuth2UserService,
      OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // Public Application Endpoints
            .requestMatchers("/api/v1/bakong/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()

            // OAuth2 endpoints (authorization request + provider callback)
            .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

            // Swagger UI & OpenAPI Docs
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html")
            .permitAll()

            .anyRequest().permitAll())
        // NEW: OAuth2 login (Google / Facebook)
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(oAuth2AuthenticationSuccessHandler));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

/* ============================================================================
   1) Add to pom.xml (inside <dependencies>):
   ============================================================================

   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-oauth2-client</artifactId>
   </dependency>


   ============================================================================
   2) Add to application.properties (values come from .env, same pattern as
      MAIL_USERNAME/MAIL_PASSWORD already use):
   ============================================================================

   spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
   spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
   spring.security.oauth2.client.registration.google.scope=email,profile

   spring.security.oauth2.client.registration.facebook.client-id=${FACEBOOK_CLIENT_ID}
   spring.security.oauth2.client.registration.facebook.client-secret=${FACEBOOK_CLIENT_SECRET}
   spring.security.oauth2.client.registration.facebook.scope=email,public_profile

   app.oauth2.redirect-uri=${OAUTH2_REDIRECT_URI:http://localhost:5173/oauth2/redirect}


   ============================================================================
   3) Add to .env (and .env.example, WITHOUT real values):
   ============================================================================

   GOOGLE_CLIENT_ID=
   GOOGLE_CLIENT_SECRET=
   FACEBOOK_CLIENT_ID=
   FACEBOOK_CLIENT_SECRET=
   OAUTH2_REDIRECT_URI=http://localhost:5173/oauth2/redirect

   ============================================================================ */