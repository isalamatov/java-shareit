package ru.practicum.shareit.item.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long, List<Item>> items;

    private long idCounter = 1L;

    public ItemRepositoryImpl() {
        this.items = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        log.debug("Create item request was received in repository {}, with data {}", this.getClass(), item.toString());
        Item createdItem = item.setId(idCounter++);
        items.computeIfPresent(createdItem.getOwner().getId(), (k, v) -> {
            v.add(item);
            return v;
        });
        items.putIfAbsent(createdItem.getOwner().getId(), new ArrayList<>(Collections.singletonList(item)));
        log.debug("Item {} was created successfully in repository {}", createdItem.toString(), this.getClass());
        return createdItem;
    }

    @Override
    public Item get(Long itemId) {
        log.debug("Get item request was received in repository {}, with data {}", this.getClass(), itemId);
        Item item = items.values().stream()
                .flatMap(Collection::stream)
                .filter(x -> x.getId() == itemId)
                .findFirst().get();
        log.debug("Item with id {} was retrieved successfully in repository {}", itemId, this.getClass());
        return item;
    }

    @Override
    public Item update(Item item) {
        log.debug("Update item request was received in repository {}, with data {}", this.getClass(), item.toString());
        items.compute(item.getOwner().getId(), (k, v) -> {
            v.removeIf(x -> x.getId() == item.getId());
            v.add(item);
            return v;
        });
        log.debug("Item {} was updated successfully in repository {}", items.toString(), this.getClass());
        return item;
    }

    @Override
    public Item partialUpdate(Long ownerId, ItemPartialUpdateDto itemPartialUpdateDto) {
        Item updatedItem = items.get(ownerId).stream()
                .filter(x -> x.getId() == itemPartialUpdateDto.getId())
                .findFirst().get();
        Field[] fields = itemPartialUpdateDto.getClass().getDeclaredFields();
        Field[] itemFields = updatedItem.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(itemPartialUpdateDto) != null) {
                    Field userField = Arrays.stream(itemFields)
                            .sequential()
                            .filter(x -> x.getName().equalsIgnoreCase(field.getName()))
                            .findFirst().get();
                    userField.setAccessible(true);
                    userField.set(updatedItem, field.get(itemPartialUpdateDto));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        items.computeIfPresent(updatedItem.getOwner().getId(), (k, v) -> {
            v.removeIf(x -> x.getId() == updatedItem.getId());
            v.add(updatedItem);
            return v;
        });
        return updatedItem;
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        log.debug("Delete itemId request was received in repository {}, with data {}", this.getClass(), itemId);
        items.computeIfPresent(ownerId, (k, v) -> {
            v.removeIf(x -> x.getId() == itemId);
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
    public List<Item> search(String text) {
        log.debug("Search items request was received in repository {}", this.getClass());
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(x -> x.getAvailable()
                        && (x.getDescription().toLowerCase().contains(text.toLowerCase())
                        || x.getName().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isItemExists(Long ownerId, Long itemId) {
        return items.containsKey(ownerId) && items.get(ownerId).stream().anyMatch(x -> x.getId() == itemId);
    }

    @Override
    public boolean isItemExists(Long itemId) {
        return items.values().stream().flatMap(Collection::stream).anyMatch(v -> v.getId() == itemId);
    }

}
