package com.example.Sparta_Store.oAuth.service;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.config.jwt.UserRoleEnum;
import com.example.Sparta_Store.oAuth.model.GoogleUser;
import com.example.Sparta_Store.oAuth.model.ProviderUser;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oauth2UserService.loadUser(userRequest);

        ProviderUser providerUser = providerUser(clientRegistration, oAuth2User);

        String provider = providerUser.getProvider();
        String providerId = providerUser.getProviderId();
        String email = providerUser.getEmail();
        String name = providerUser.getName();

        // 회원 가입
        if (userRepository.findByProviderAndProviderId(provider,providerId) == null) {
            User user = new User (
                    provider,
                    providerId,
                    name,
                    email,
                    new Address("","",""),
                    UserRoleEnum.USER
            );
            userRepository.save(user);
        }

        // 인증 처리
        return oAuth2User;
    }

    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        String registrationId = clientRegistration.getRegistrationId();

        if (Objects.equals(registrationId, "google")) {
            return new GoogleUser(oAuth2User, clientRegistration);
        }

        return null;
    }
}