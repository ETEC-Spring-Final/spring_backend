package com.example.spring_boot_project_api.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * FIX (Phase A follow-up): SecurityConfig's oauth2Login(...) never had a
 * failureHandler configured, which caused two separate problems:
 *
 * 1. Spring Security only logs the real AuthenticationException at DEBUG
 *    level. At the app's normal INFO level that exception is completely
 *    invisible, so every failed Google/Facebook login just looked like a
 *    silent 401 with no clue why.
 * 2. The built-in default failure handler
 *    (SimpleUrlAuthenticationFailureHandler with no defaultFailureUrl set)
 *    redirects to "/login?error" when one IS configured elsewhere, or
 *    calls response.sendError(401) directly otherwise — and "/login" by
 *    itself is not in SecurityConfig's permitAll list, so even the
 *    failure redirect itself 401'd a second time (the "/login?error"
 *    page also returning 401 seen in the browser).
 *
 * This handler logs the exception at ERROR (so it always shows up in the
 * console, whatever the configured logging level), cleans up the temporary
 * cookie from CookieOAuth2AuthorizationRequestRepository, and sends the
 * browser back to the Vue app's /login page with a readable reason instead
 * of a bare backend error page.
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

  private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

  private final CookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

  // Same env var as OAuth2AuthenticationSuccessHandler's redirect-uri, minus
  // the "/oauth2/redirect" suffix, so both handlers point at the same
  // frontend origin without duplicating an extra property.
  @Value("${app.oauth2.redirect-uri:${OAUTH2_REDIRECT_URI:http://localhost:5173/oauth2/redirect}}")
  private String successRedirectUri;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    // THIS is the line to watch in the terminal: it prints the actual
    // cause (invalid_client, redirect_uri_mismatch, a NullPointerException
    // from our own loadUser() code, etc.) instead of it disappearing into
    // DEBUG-only logging.
    log.error("OAuth2 login failed: {}", exception.getMessage(), exception);

    cookieAuthorizationRequestRepository.removeAuthorizationRequestCookie(response);

    String frontendLoginUrl = successRedirectUri.replace("/oauth2/redirect", "/login");
    String reason = exception.getMessage() != null ? exception.getMessage() : "oauth2_login_failed";
    String targetUrl = frontendLoginUrl + "?oauthError=" + URLEncoder.encode(reason, StandardCharsets.UTF_8);

    response.sendRedirect(targetUrl);
  }
}