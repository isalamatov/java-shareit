package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;

import javax.validation.Valid;

/**
 * Items REST API controller, managing HTTP requests.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Create item request was received in controller {} with data {}", this.getClass(), itemDto.toString());
        return itemClient.create(ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Get item request is received in controller {}, with id {}", this.getClass(), itemId);
        return itemClient.getItem(userId, itemId);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Update request was received in controller {} with data {}", this.getClass(), itemDto.toString());
        return itemClient.update(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> partialUpdate(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody ItemPartialUpdateDto itemPartialUpdateDto) {
        log.info("Update request was received in controller {} with data {}",
                this.getClass(),
                itemPartialUpdateDto.toString());
        return itemClient.partialUpdate(ownerId, itemId, itemPartialUpdateDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId) {
        log.info("Delete request is received in controller {}, with id {}", this.getClass(), itemId);
        itemClient.deleteItem(ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get all items request is received in controller {}", this.getClass());
        return itemClient.getAll(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Search items request is received in controller {} with text {}", this.getClass(), text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid
                                 @RequestBody CommentDto commentDto) {
        log.info("Create comment request was received in controller {} with data {}", this.getClass(), commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
