package com.example.Sparta_Store.util;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int currentPage,
        int totalPage,
        int totalCount
) {

    public static <T> PageResult<T> from(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                (int) page.getTotalElements()
        );
    }
}
