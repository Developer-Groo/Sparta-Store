package com.example.Sparta_Store.config;

import com.example.Sparta_Store.oAuth.handler.CustomOAuth2SuccessHandler;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import com.example.Sparta_Store.config.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/signUp", "/users/login", "/login", "/css/**", "/oauth2/**", "/auth/login").permitAll() // 회원가입, 로그인, CSS 파일 요청 허용
                .requestMatchers(HttpMethod.GET, "/items", "/items/**").permitAll() // 아이템 조회 허용
                .requestMatchers("/admin/**").hasRole("ADMIN") //admin 이 붙은것은 ADMIN 이 존재해야만 통과 나머지는 누구나 가능하게 했습니다.
                .anyRequest().authenticated()
            );

        http.oauth2Login(oauth -> oauth.successHandler(customOAuth2SuccessHandler));

        return http.build();
    }
}