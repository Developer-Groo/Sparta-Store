package com.example.Sparta_Store.oAuth.model;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class GoogleUser extends OAuth2ProviderUser{
    public GoogleUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getName() {
        System.out.println(getAttributes());

        return (String) getAttributes().get("name");
    }

    @Override
    public String getProviderId() {
        return getAttributes().get("sub").toString();
    }
}
