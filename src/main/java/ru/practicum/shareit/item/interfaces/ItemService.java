package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item get(Long itemId);

    Item update(Long ownerId, Item item);

    Item partialUpdate(Long ownerId, ItemPartialUpdateDto itemPartialUpdateDto);

    void delete(Long ownerId, Long itemId);

    List<Item> getAll(Long ownerId);

    List<Item> search(String text);
}
