package ru.practicum.shareit.request.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.request.exceptions.ItemRequestDoesNotExistException;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.QItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        log.debug("Create item request received in controller {}", this.getClass());
        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);
        log.debug("Create item request successfully retrieved from repository");
        return createdItemRequest;
    }

    @Override
    public List<ItemRequest> getAllByRequestor(User requestor) {
        log.debug("Get all by requestor item request received in controller {}", this.getClass());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor(requestor);
        log.debug("List of item requests successfully retrieved from repository");
        return itemRequests;
    }

    @Override
    public Iterable<ItemRequest> getAll(User user, Integer from, Integer size) {
        log.debug("Get all by user item request received in controller {}", this.getClass());
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Offset and limit should be correct values");
        }
        BooleanExpression condition = QItemRequest.itemRequest.requestor.eq(user).not();
        Sort sortOrder = Sort.by("created").descending();
        Integer pageNumber = from / size;
        Pageable page = PageRequest.of(pageNumber, size, sortOrder);
        Iterable<ItemRequest> itemRequests = itemRequestRepository.findAll(condition, page);
        log.debug("List of item requests successfully retrieved from repository");
        return itemRequests;
    }

    @Override
    public ItemRequest getById(Long requestId) {
        log.debug("Get by id item request received in controller {}", this.getClass());
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestDoesNotExistException(requestId));
        log.debug("Item request successfully retrieved from repository");
        return itemRequest;
    }
}
