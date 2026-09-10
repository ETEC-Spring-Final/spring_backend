package com.example.spring_boot_project_api.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.spring_boot_project_api.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Fires once Spring Security has finished the OAuth2 handshake and
 * CustomOAuth2UserService has resolved/created our User row. We don't use a
 * server-side session for the SPA — instead we mint our own JWT (same
 * JwtUtil used by /api/auth/login) and hand it to the frontend via a
 * redirect query param.
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    // Where the Vue app should land after a successful OAuth login. Configure via
    // env var so dev/prod can point at different frontend origins.
    @Value("${app.oauth2.redirect-uri:${OAUTH2_REDIRECT_URI:http://localhost:5173/oauth2/redirect}}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(principal.getUser());

        String targetUrl = redirectUri + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}