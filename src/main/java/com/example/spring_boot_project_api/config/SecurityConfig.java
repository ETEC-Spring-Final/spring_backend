package com.example.spring_boot_project_api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

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
      OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
      JwtAuthFilter jwtAuthFilter) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Preflight requests must always be allowed through, unauthenticated,
            // before any other rule is evaluated.
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/v1/bakong/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html")
            .permitAll()

            // ===== Public browsing endpoints (Explore / vehicle detail pages
            // used before login) — must stay in sync with router/index.js
            // routes that have no requiresAuth meta. =====
            .requestMatchers(HttpMethod.GET, "/api/vehicles/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/locations/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/reviews/vehicle/**").permitAll()

            // Site branding/contact info (logo, site name, contact email/phone) —
            // must be readable WITHOUT a JWT: the login page, the public
            // marketing site header/footer, and the admin dashboard sidebar/
            // header all render this before or without authentication.
            // Updating it (PUT) still requires ADMIN/MANAGER via @PreAuthorize
            // on SiteSettingsController.
            .requestMatchers(HttpMethod.GET, "/api/settings").permitAll()

            // "my-reviews" must stay authenticated even though it sits under
            // /api/reviews — list it BEFORE the generic single-review rule
            // below so it is matched first.
            .requestMatchers(HttpMethod.GET, "/api/reviews/my-reviews").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/reviews/*").permitAll()

            // Everything else (notifications, /api/auth/users, all
            // POST/PUT/PATCH/DELETE, dashboard data, etc.) requires a valid,
            // authenticated JWT. Role-specific restrictions are still
            // enforced separately via @PreAuthorize on each method.
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(oAuth2AuthenticationSuccessHandler))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
            .accessDeniedHandler((request, response, accessDeniedException) ->
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")));

    return http.build();  
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}