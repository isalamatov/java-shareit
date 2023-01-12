package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface ItemRequestMapper {

    @Mapping(target = "itemRequestId", source = "id", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ItemRequest toEntity(ItemRequestDto itemRequestDto);

    @Mapping(source = "itemRequestId", target = "id")
    ItemRequestDto toDto(ItemRequest itemRequest);

    List<ItemRequestDto> toDto(Collection<ItemRequest> itemRequest);

    List<ItemRequestDto> toDto(Iterable<ItemRequest> itemRequest);

}
