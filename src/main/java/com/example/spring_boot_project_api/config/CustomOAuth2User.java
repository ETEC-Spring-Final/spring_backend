package com.example.spring_boot_project_api.config;

import com.example.spring_boot_project_api.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * Wraps Spring Security's OAuth2User so we can carry our own User entity
 * alongside the raw provider attributes. The success handler reads
 * getUser() to generate our app's JWT (via JwtUtil) instead of relying on
 * a Spring Security session.
 */
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User delegate;
    private final User user;

    public CustomOAuth2User(OAuth2User delegate, User user) {
        this.delegate = delegate;
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}