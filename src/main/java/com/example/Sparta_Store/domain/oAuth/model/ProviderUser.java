package com.example.Sparta_Store.domain.oAuth.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface ProviderUser {
    String getName();

    String getEmail();

    String getProvider();

    String getProviderId();

    List<? extends GrantedAuthority> getAuthorities();
}
