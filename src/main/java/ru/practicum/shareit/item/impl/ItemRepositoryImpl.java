package ru.practicum.shareit.item.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private HashMap<Long, List<Item>> items;

    public ItemRepositoryImpl() {
        this.items = new HashMap<>();
    }
    private long idCounter = 0L;

    @Override
    public Item create(Item item) {
        log.debug("Create item request was received in repository {}, with data {}", this.getClass(), item.toString());
        Item createdItem = item.setItemId(item.getItemId() != null ? item.getItemId() : idCounter++);
        items.putIfAbsent(createdItem.getOwner().getUserId(), new ArrayList<>(Collections.singletonList(item)));
        items.computeIfPresent(createdItem.getOwner().getUserId(), (k, v) -> {
            v.add(item);
            return v;
        });
        log.debug("Item {} was created successfully in repository {}", createdItem.toString(), this.getClass());
        return createdItem;
    }

    @Override
    public Item get(Long ownerId, Long itemId) {
        log.debug("Get item request was received in repository {}, with data {}", this.getClass(), itemId);
        Item item = items.get(ownerId).stream().filter(x -> x.getItemId() == itemId).findFirst().get();
        log.debug("Item with id {} was retrieved successfully in repository {}", itemId, this.getClass());
        return item;
    }

    @Override
    public Item update(Item item) {
        log.debug("Update item request was received in repository {}, with data {}", this.getClass(), item.toString());
        items.compute(item.getOwner().getUserId(), (k, v) -> {
            v.removeIf(x -> x.getItemId() == item.getItemId());
            v.add(item);
            return v;
        });
        log.debug("Item {} was updated successfully in repository {}", items.toString(), this.getClass());
        return item;
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        log.debug("Delete itemId request was received in repository {}, with data {}", this.getClass(), itemId);
        items.computeIfPresent(ownerId, (k, v) -> {
            v.removeIf(x -> x.getItemId() == itemId);
            return v;
        });
        log.debug("Item with id {} was deleted successfully from repository {}", itemId, this.getClass());
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        log.debug("Get all items request was received in repository {}", this.getClass());
        return items.get(ownerId);
    }

    @Override
    public boolean isItemExists(Long ownerId, Long itemId) {
        return items.containsKey(ownerId) && items.get(ownerId).stream().anyMatch(x -> x.getItemId() == itemId);
    }

}
