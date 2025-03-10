package com.example.Sparta_Store.util;

import java.io.Serializable;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int currentPage,
        int totalPage,
        int totalCount
) implements Serializable { // 레디스를 사용하기 위한 직렬화 추가

    public static <T> PageResult<T> from(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                (int) page.getTotalElements()
        );
    }
}
