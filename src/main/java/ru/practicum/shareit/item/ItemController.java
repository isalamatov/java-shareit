package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Items REST API controller, managing HTTP requests.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    private final UserService userService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.debug("Create request was received in controller {} with data {}", this.getClass(), itemDto.toString());
        Item item = ItemMapper.toItem(itemDto).setOwner(userService.get(ownerId));
        Item createdItem = itemService.create(item);
        log.debug("Item {} was created successfully in controller {}", createdItem.toString(), this.getClass());
        return ItemMapper.toItemDto(createdItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.debug("Get item request is received in controller {}, with id {}", this.getClass(), itemId);
        Item item = itemService.get(itemId);
        log.debug("Item {} was retrieved successfully from controller {}", item.toString(), this.getClass());
        return ItemMapper.toItemDto(item);
    }

    @PutMapping
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.debug("Update request was received in controller {} with data {}", this.getClass(), itemDto.toString());
        Item item = ItemMapper.toItem(itemDto);
        Item updatedItem = itemService.update(ownerId, item);
        log.debug("Item {} was updated successfully in controller {}", updatedItem.toString(), this.getClass());
        return ItemMapper.toItemDto(updatedItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto partialUpdate(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody ItemPartialUpdateDto itemPartialUpdateDto) {
        log.debug("Update request was received in controller {} with data {}",
                this.getClass(),
                itemPartialUpdateDto.toString());
        itemPartialUpdateDto.setId(itemId);
        Item updatedItem = itemService.partialUpdate(ownerId, itemPartialUpdateDto);
        log.debug("Item {} was updated successfully in controller {}", updatedItem.toString(), this.getClass());
        return ItemMapper.toItemDto(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId) {
        log.debug("Delete request is received in controller {}, with id {}", this.getClass(), itemId);
        itemService.delete(ownerId, itemId);
        log.debug("Item with id {} was deleted successfully in controller {}", itemId, this.getClass());
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.debug("Get all items request is received in controller {}", this.getClass());
        List<Item> items = itemService.getAll(ownerId);
        log.debug("All items were retrieved successfully from controller {}", this.getClass());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.debug("Search items request is received in controller {} with text {}", this.getClass(), text);
        List<Item> items = itemService.search(text);
        log.debug("Items were retrieved successfully from controller {}", this.getClass());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
