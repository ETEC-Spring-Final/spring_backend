package com.example.spring_boot_project_api.config;

import com.example.spring_boot_project_api.enums.AuthProviderEnum;
import com.example.spring_boot_project_api.enums.GenderEnum;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Loads the Google/Facebook profile after the provider redirects back, then
 * either creates a new User row or links an existing LOCAL account with the
 * same email. Does NOT touch our JWT — that happens in
 * OAuth2AuthenticationSuccessHandler, which reads getUser() off the
 * CustomOAuth2User this method returns.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google" or "facebook"
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String firstName;
        String lastName;
        String providerId;
        AuthProviderEnum provider;

        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            firstName = (String) attributes.getOrDefault("given_name", "");
            lastName = (String) attributes.getOrDefault("family_name", "");
            providerId = String.valueOf(attributes.get("sub"));
            provider = AuthProviderEnum.GOOGLE;
        } else if ("facebook".equals(registrationId)) {
            email = (String) attributes.get("email");
            String fullName = (String) attributes.getOrDefault("name", "");
            String[] parts = fullName.trim().split(" ", 2);
            firstName = parts.length > 0 ? parts[0] : "User";
            lastName = parts.length > 1 ? parts[1] : "";
            providerId = String.valueOf(attributes.get("id"));
            provider = AuthProviderEnum.FACEBOOK;
        } else {
            throw new OAuth2AuthenticationException("Unsupported OAuth provider: " + registrationId);
        }

        if (email == null || email.isBlank()) {
            // Facebook only returns email if the user granted that permission and has
            // a verified email on file — without it we cannot map to our User table.
            throw new OAuth2AuthenticationException(
                "No email returned by " + registrationId + ". Please make sure email access is granted.");
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // New user — build an account from the OAuth profile.
            // password/phone/gender are NOT NULL columns on User, but OAuth providers
            // don't supply real values for them, so we fill placeholders here and rely
            // on the frontend to prompt the user to complete their profile afterward.
            user = User.builder()
                .firstName(firstName.isBlank() ? "User" : firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // unusable random password
                .phone("0000000000") // placeholder — prompt user to update later
                .gender(GenderEnum.MALE) // placeholder — prompt user to update later
                .role(RoleEnum.CUSTOMER)
                .authProvider(provider)
                .providerId(providerId)
                .active(true)
                .build();
            userRepository.save(user);
        } else if (user.getAuthProvider() == AuthProviderEnum.LOCAL) {
            // An existing local (email/password) account shares this email — link it to
            // this OAuth provider instead of creating a duplicate account.
            user.setAuthProvider(provider);
            user.setProviderId(providerId);
            userRepository.save(user);
        }
        // else: user already linked to this (or another) OAuth provider — just log them in.

        return new CustomOAuth2User(oAuth2User, user);
    }
}