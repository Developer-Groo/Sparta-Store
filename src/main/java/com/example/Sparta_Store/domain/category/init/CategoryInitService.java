package com.example.Sparta_Store.domain.category.init;

import com.example.Sparta_Store.domain.category.entity.Category;
import com.example.Sparta_Store.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryInitService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void initialize() {
        if (categoryRepository.count() > 0) return;

        Category clothing = new Category("의류", null);
        Category tops = new Category("상의", clothing);
        Category bottoms = new Category("하의", clothing);

        Category shoes = new Category("신발", null);
        Category sneakers = new Category("운동화", shoes);
        Category slippers = new Category("샌들", shoes);

        Category accessories = new Category("액세서리", null);
        Category bags = new Category("가방", accessories);
        Category hats = new Category("모자", accessories);

        // 상위 카테고리에 하위 카테고리 연결
        clothing.addChildren(tops, bottoms);
        shoes.addChildren(sneakers, slippers);
        accessories.addChildren(bags, hats);

        categoryRepository.saveAll(List.of(clothing, shoes, accessories));
    }
}
