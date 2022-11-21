package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;
import ru.practicum.shareit.item.exceptions.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exceptions.ItemDoesNotExistException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public Item create(Item item) {
        log.debug("Create item request was received in service {}, with data {}", this.getClass(), item.toString());
        Item createdItem;
        if (!userRepository.isUserExists(item.getOwner().getId())) {
            throw new UserDoesNotExistException(item.getOwner().getId());
        }
        if (item.getId() == null || !itemRepository.isItemExists(item.getOwner().getId(), item.getId())) {
            createdItem = itemRepository.create(item);
        } else {
            throw new ItemAlreadyExistsException(item.getId());
        }
        log.debug("Item {} was created successfully in service {}", createdItem.toString(), this.getClass());
        return createdItem;
    }

    @Override
    public Item get(Long itemId) {
        log.debug("Get item request is received in service {}, with id {}", this.getClass(), itemId);
        Item item;
        if (itemRepository.isItemExists(itemId)) {
            item = itemRepository.get(itemId);
        } else {
            throw new ItemDoesNotExistException(itemId);
        }
        log.debug("Item {} was retrieved successfully from service {}", item.toString(), this.getClass());
        return item;
    }

    @Override
    public Item update(Long ownerId, Item item) {
        log.debug("Update item request was received in service {}, with data {}", this.getClass(), item.toString());
        Item updatedItem;
        if (userRepository.isUserExists(ownerId)) {
            item.setOwner(userRepository.get(ownerId));
        } else {
            throw new UserDoesNotExistException(ownerId);
        }
        if (itemRepository.isItemExists(ownerId, item.getId())) {
            updatedItem = itemRepository.update(item);
        } else {
            throw new ItemDoesNotExistException(item.getId());
        }
        log.debug("Item {} was updated successfully in service {}", updatedItem.toString(), this.getClass());
        return updatedItem;
    }

    @Override
    public Item partialUpdate(Long ownerId, ItemPartialUpdateDto itemPartialUpdateDto) {
        log.debug("Update item request was received in service {}, with data {}", this.getClass(),
                itemPartialUpdateDto.toString());
        Item updatedItem;
        if (!userRepository.isUserExists(ownerId)) {
            throw new UserDoesNotExistException(ownerId);
        }
        if (itemRepository.isItemExists(ownerId, itemPartialUpdateDto.getId())) {
            updatedItem = itemRepository.partialUpdate(ownerId, itemPartialUpdateDto);
        } else {
            throw new ItemDoesNotExistException(itemPartialUpdateDto.getId());
        }
        log.debug("Item {} was updated successfully in service {}", updatedItem.toString(), this.getClass());
        return updatedItem;
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        log.debug("Delete item request is received in controller {}, with id {}", this.getClass(), itemId);
        if (!userRepository.isUserExists(ownerId)) {
            throw new UserDoesNotExistException(ownerId);
        }
        if (itemRepository.isItemExists(ownerId, itemId)) {
            itemRepository.delete(ownerId, itemId);
        } else {
            throw new ItemDoesNotExistException(itemId);
        }
        log.debug("Item with id {} was deleted successfully in service {}", itemId, this.getClass());
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        log.debug("Get all items request is received in service {}", this.getClass());
        if (!userRepository.isUserExists(ownerId)) {
            throw new UserDoesNotExistException(ownerId);
        }
        return itemRepository.getAll(ownerId);
    }

    @Override
    public List<Item> search(String text) {
        log.debug("Search items request is received in service {}", this.getClass());
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }
}
