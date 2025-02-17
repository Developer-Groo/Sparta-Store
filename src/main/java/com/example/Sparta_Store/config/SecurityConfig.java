package com.example.Sparta_Store.config;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/signUp","/users/login").permitAll() //회원가입과 로그인은 인증없이 가능
                .requestMatchers(HttpMethod.GET, "/items", "/items/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN") //admin 이 붙은것은 ADMIN 이 존재해야만 통과 나머지는 누구나 가능하게 했습니다.
                .anyRequest().authenticated()
            )
            .build();
    }
}