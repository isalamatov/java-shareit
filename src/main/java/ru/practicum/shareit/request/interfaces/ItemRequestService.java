package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(ItemRequest itemRequest);

    List<ItemRequest> getAllByRequestor(User requestor);

    Iterable<ItemRequest> getAll(User user, Integer from, Integer size);

    ItemRequest getById(Long requestId);
}
