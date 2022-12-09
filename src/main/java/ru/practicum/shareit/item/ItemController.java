package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;
import ru.practicum.shareit.comments.interfaces.CommentService;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
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
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final UserMapper userMapper;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.debug("Create request was received in controller {} with data {}", this.getClass(), itemDto.toString());
        User owner = userService.get(ownerId);
        UserDto ownerDto = userMapper.toUserDto(owner);
        Item item = mapper.toItem(itemDto.setOwner(ownerDto));
        Item createdItem = itemService.create(item);
        log.debug("Item {} was created successfully in controller {}", createdItem.toString(), this.getClass());
        return mapper.toItemDto(createdItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.debug("Get item request is received in controller {}, with id {}", this.getClass(), itemId);
        ItemDto itemDto = itemService.get(itemId, userId);
        log.debug("Item {} was retrieved successfully from controller {}", itemDto.toString(), this.getClass());
        return itemDto;
    }

    @PutMapping
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.debug("Update request was received in controller {} with data {}", this.getClass(), itemDto.toString());
        Item item = mapper.toItem(itemDto);
        Item updatedItem = itemService.update(ownerId, item);
        log.debug("Item {} was updated successfully in controller {}", updatedItem.toString(), this.getClass());
        return mapper.toItemDto(updatedItem);
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
        return mapper.toItemDto(updatedItem);
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
        List<ItemDto> items = itemService.getAll(ownerId);
        log.debug("All items were retrieved successfully from controller {}", this.getClass());
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.debug("Search items request is received in controller {} with text {}", this.getClass(), text);
        List<Item> items = itemService.search(text);
        log.debug("Items were retrieved successfully from controller {}", this.getClass());
        return items.stream().map(mapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @Valid
                              @RequestBody CommentDto commentDto) {
        log.debug("Create comment request was received in controller {} with data {}", this.getClass(), commentDto);
        Comment comment = commentMapper.toComment(commentDto);
        User author = userService.get(userId);
        comment.setAuthor(author);
        Comment createdComment = commentService.create(comment, itemId);
        log.debug("Comment {} was created successfully in controller {}", createdComment, this.getClass());
        return commentMapper.toDto(createdComment);
    }
}
