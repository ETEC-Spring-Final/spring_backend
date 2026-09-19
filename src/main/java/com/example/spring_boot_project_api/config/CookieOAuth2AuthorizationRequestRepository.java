package com.example.spring_boot_project_api.config;

import java.util.Base64;
import java.util.Optional;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FIX (Phase A follow-up): Google/Facebook login was failing with a 401 at
 * /login/oauth2/code/{provider} on every attempt.
 *
 * Root cause: SecurityConfig sets SessionCreationPolicy.STATELESS for the
 * JWT-based API. Spring Security's OAuth2 login flow, however, stores the
 * in-flight OAuth2AuthorizationRequest — including the "state" value used to
 * prevent CSRF — in the HttpSession by default, via
 * HttpSessionOAuth2AuthorizationRequestRepository. With session creation
 * disabled, nothing is ever persisted between "redirect to Google" and
 * "Google redirects back", so when the callback hits
 * /login/oauth2/code/google, Spring cannot find a matching authorization
 * request and throws an OAuth2AuthenticationException. With no custom
 * failure handler configured, Spring's default failure handler turns that
 * into exactly the bare 401 seen in the browser at port 8080.
 *
 * This repository moves that short-lived state out of the (disabled)
 * session and into an HttpOnly cookie instead, so the OAuth2 handshake works
 * without turning session creation back on for the rest of the stateless
 * JWT API.
 */
@Component
public class CookieOAuth2AuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  public static final String COOKIE_NAME = "oauth2_auth_request";
  // The whole "redirect to provider, come straight back" round-trip should
  // take a few seconds, not minutes — keep this short-lived on purpose.
  private static final int COOKIE_MAX_AGE_SECONDS = 300;

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    return getCookie(request)
        .map(this::deserialize)
        .orElse(null);
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request, HttpServletResponse response) {
    if (authorizationRequest == null) {
      removeAuthorizationRequestCookie(response);
      return;
    }
    Cookie cookie = new Cookie(COOKIE_NAME, serialize(authorizationRequest));
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
    response.addCookie(cookie);
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
      HttpServletResponse response) {
    OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
    removeAuthorizationRequestCookie(response);
    return authorizationRequest;
  }

  /**
   * Called from OAuth2AuthenticationSuccessHandler once login has actually
   * succeeded, so the temporary cookie doesn't linger in the browser.
   */
  public void removeAuthorizationRequestCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie(COOKIE_NAME, "");
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  private Optional<Cookie> getCookie(HttpServletRequest request) {
    return Optional.ofNullable(WebUtils.getCookie(request, COOKIE_NAME));
  }

  private String serialize(OAuth2AuthorizationRequest authorizationRequest) {
    // OAuth2AuthorizationRequest implements Serializable, so plain Java
    // serialization + base64 is enough here — no need for a Jackson mixin.
    return Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(authorizationRequest));
  }

  private OAuth2AuthorizationRequest deserialize(Cookie cookie) {
    return (OAuth2AuthorizationRequest) SerializationUtils
        .deserialize(Base64.getUrlDecoder().decode(cookie.getValue()));
  }
}