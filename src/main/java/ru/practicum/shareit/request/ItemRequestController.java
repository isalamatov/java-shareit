package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Items REST API controller, managing HTTP requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Create item request was received in controller {} with data {}",
                this.getClass(),
                itemRequestDto.toString());
        User requestor = userService.get(requestorId);
        UserDto requestorDto = userMapper.toUserDto(requestor);
        itemRequestDto.setRequestor(requestorDto);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest createdItemRequest = itemRequestService.create(itemRequestMapper.toEntity(itemRequestDto));
        log.debug("Item request {} was created successfully in controller {}",
                createdItemRequest.toString(),
                this.getClass());
        return itemRequestMapper.toDto(createdItemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.debug("Get all item requests was received in controller {} with data {}",
                this.getClass(),
                requestorId);
        User requestor = userService.get(requestorId);
        List<ItemRequest> items = itemRequestService.getAllByRequestor(requestor);
        log.debug("Get all item requests {} was retrieved successfully in controller {}",
                items.toString(),
                this.getClass());
        return itemRequestMapper.toDto(items);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                       @RequestParam(required = false, defaultValue = "25") Integer size) {
        log.debug("Get all item requests was received in controller {} with data {}",
                this.getClass(),
                userId);
        User user = userService.get(userId);
        Iterable<ItemRequest> items = itemRequestService.getAll(user, from, size);
        log.debug("Get all item requests {} was retrieved successfully in controller {}",
                items.toString(),
                this.getClass());
        return itemRequestMapper.toDto(items);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long requestId) {
        log.debug("Get item request was received in controller {} with id {}",
                this.getClass(),
                requestId);
        User user = userService.get(userId);
        ItemRequest item = itemRequestService.getById(requestId);
        log.debug("Item request {} was retrieved successfully in controller {}",
                item.toString(),
                this.getClass());
        return itemRequestMapper.toDto(item);
    }
}
