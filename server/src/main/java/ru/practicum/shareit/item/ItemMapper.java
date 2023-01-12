package ru.practicum.shareit.item;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        uses = {ItemRequestService.class, UserMapper.class, ItemRequestMapper.class})
public interface ItemMapper {

    @Mapping(target = "request", source = "requestId", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Item toItem(ItemDto itemDto);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "requestId", source = "request.itemRequestId")
    ItemDto toItemDto(Item item);
}
