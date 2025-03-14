package com.example.Sparta_Store.email.event;

import com.example.Sparta_Store.domain.item.entity.Item;

import java.util.List;

public record ItemRestockedEvent(
        Long itemId,
        String name,
        List<String> userEmails
) {
    public static ItemRestockedEvent toEvent(Item item, List<String> emails) {
        return new ItemRestockedEvent(
                item.getId(),
                item.getName(),
                emails
        );
    }
}
