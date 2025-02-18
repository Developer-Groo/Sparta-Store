package com.example.Sparta_Store.category.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInitializer {

    private final CategoryInitService categoryInitService;

    @Bean
    public ApplicationRunner initializeCategories() {
        return args -> categoryInitService.initialize();
    }
}
