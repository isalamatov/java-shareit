package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "request", ignore = true)
    Item toItem(ItemDto itemDto);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "request", ignore = true)
    ItemDto toItemDto(Item item);

    List<ItemDto> toItemDto(Collection<Item> items);

    List<Item> toItem(Collection<ItemDto> itemDtos);
}
