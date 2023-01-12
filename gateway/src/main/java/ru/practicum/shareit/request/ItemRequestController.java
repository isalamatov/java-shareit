package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

/**
 * Items REST API controller, managing HTTP requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create item request was received in controller {} with data {}",
                this.getClass(),
                itemRequestDto.toString());
        return itemRequestClient.create(requestorId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Get all item requests was received in controller {} with data {}",
                this.getClass(),
                requestorId);
        return itemRequestClient.getAllByRequestor(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @RequestParam(required = false, defaultValue = "25") Integer size) {
        log.info("Get all item requests was received in controller {} with data {}",
                this.getClass(),
                userId);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        log.info("Get item request was received in controller {} with id {}",
                this.getClass(),
                requestId);
        return itemRequestClient.getById(userId, requestId);
    }
}
