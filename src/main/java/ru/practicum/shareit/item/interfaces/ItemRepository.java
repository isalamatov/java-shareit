package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Item item);

    Item get(Long ownerId, Long itemId);

    Item update(Item item);

    void delete(Long ownerId, Long itemId);

    List<Item> getAll(Long ownerId);

    boolean isItemExists(Long ownerId, Long itemId);
}
