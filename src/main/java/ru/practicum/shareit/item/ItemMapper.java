package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return new Item()
                .setItemId(itemDto.getItemId())
                .setName(itemDto.getName())
                .setDescription(itemDto.getDescription())
                .setAvailable(itemDto.getAvailable());
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto()
                .setItemId(item.getItemId())
                .setName(item.getName())
                .setDescription(item.getDescription())
                .setAvailable(item.getAvailable())
                .setRequest(item.getRequest() != null ? item.getRequest().getItemRequestId() : null);
    }
}
