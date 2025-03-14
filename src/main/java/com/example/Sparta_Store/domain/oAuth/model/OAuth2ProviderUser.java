package com.example.Sparta_Store.domain.oAuth.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

public abstract class OAuth2ProviderUser implements ProviderUser {

    private final OAuth2User oAuth2User;
    private final ClientRegistration clientRegistration;

    @Getter
    private final Map<String, Object> attributes;

    public OAuth2ProviderUser(Map<String, Object> attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        this.attributes = oAuth2User.getAttributes();
        this.oAuth2User = oAuth2User;
        this.clientRegistration = clientRegistration;
    }

    @Override
    public String getProvider() {
        return clientRegistration.getRegistrationId();
    }

    @Override
    public String getEmail() {
        if (attributes == null || !attributes.containsKey("email")) {
            throw new IllegalArgumentException("Email attribute is missing");
        }

        Object emailObject = attributes.get("email");
        System.out.println("Email object: " + emailObject); // 추가된 로그

        if (emailObject == null) {
            throw new IllegalArgumentException("Email attribute is null");
        }
        return emailObject.toString();
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities()
                .stream()
                .map(authority ->
                        new SimpleGrantedAuthority(authority.getAuthority())
                )
                .toList();
    }
}
