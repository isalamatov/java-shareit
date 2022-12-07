package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ItemMapper {
    public Item toItem(ItemDto itemDto) {
        return new Item()
                .setId(itemDto.getId())
                .setName(itemDto.getName())
                .setDescription(itemDto.getDescription())
                .setAvailable(itemDto.getAvailable());
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto()
                .setId(item.getId())
                .setName(item.getName())
                .setDescription(item.getDescription())
                .setAvailable(item.getAvailable())
                .setRequest(item.getRequest() != null ? item.getRequest().getItemRequestId() : null)
                .setLastBooking(null)
                .setNextBooking(null)
                .setComments(null);
    }

    public List<ItemDto> toItemDto(Collection<Item> items) {
        if (items == null) {
            return null;
        }

        List<ItemDto> list = new ArrayList<>(items.size());
        for (Item item : items) {
            list.add(toItemDto(item));
        }

        return list;
    }

    public List<Item> toItem(Collection<ItemDto> itemDtos) {
        if (itemDtos == null) {
            return null;
        }

        List<Item> list = new ArrayList<>(itemDtos.size());
        for (ItemDto itemDto : itemDtos) {
            list.add(toItem(itemDto));
        }

        return list;
    }
}
