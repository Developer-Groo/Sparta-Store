package com.example.Sparta_Store.init.users;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersInitializer {

    private final UsersInitService usersInitService;

    @Bean
    public ApplicationRunner initializeUsers() {
        return args -> usersInitService.initialize();
    }
}
