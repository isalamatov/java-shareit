package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exceptions.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exceptions.ItemDoesNotExistException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserRepository;

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
        if (item.getItemId() == null || !itemRepository.isItemExists(item.getOwner().getUserId(), item.getItemId())) {
            createdItem = itemRepository.create(item);
        } else {
            throw new ItemAlreadyExistsException(item.getItemId());
        }
        log.debug("Item {} was created successfully in service {}", createdItem.toString(), this.getClass());
        return createdItem;
    }

    @Override
    public Item get(Long ownerId, Long itemId) {
        log.debug("Get item request is received in service {}, with id {}", this.getClass(), itemId);
        Item item;
        if (!userRepository.isUserExists(ownerId)) {
            throw new UserDoesNotExistException(ownerId);
        }
        if (itemRepository.isItemExists(ownerId, itemId)) {
            item = itemRepository.get(ownerId, itemId);
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
        if (itemRepository.isItemExists(ownerId, item.getItemId())) {
            updatedItem = itemRepository.update(item);
        } else {
            throw new ItemDoesNotExistException(item.getItemId());
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
}
