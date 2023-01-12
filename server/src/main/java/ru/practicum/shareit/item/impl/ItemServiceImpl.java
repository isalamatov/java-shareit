package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.interfaces.CommentRepository;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;
import ru.practicum.shareit.item.exceptions.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exceptions.ItemDoesNotExistException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.exceptions.ItemRequestDoesNotExistException;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;

    @Override
    public Item create(Item item) {
        log.debug("Create item request was received in service {}, with data {}", this.getClass(), item.toString());
        Item createdItem;
        if (!userRepository.existsById(item.getOwner().getId())) {
            throw new UserDoesNotExistException(item.getOwner().getId());
        }
        if (item.getId() == null || !itemRepository.existsById(item.getId())) {
            createdItem = itemRepository.save(item);
        } else {
            throw new ItemAlreadyExistsException(item.getId());
        }
        log.debug("Item {} was created successfully in service {}", createdItem, this.getClass());
        return createdItem;
    }

    @Override
    public ItemDto get(Long itemId, Long userId) {
        log.debug("Get item request is received in service {}, with id {}", this.getClass(), itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemDoesNotExistException(itemId));
        ItemDto itemDto = mapper.toItemDto(item);
        if (item.getOwner().getId() == userId) {
            Optional<Booking> lastBookingOpt = bookingRepository.findAllByItem(item)
                    .stream()
                    .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                    .filter(x -> x.getEnd().isBefore(LocalDateTime.now())).max(Comparator.comparing(Booking::getEnd));
            Booking lastBooking = lastBookingOpt.orElse(null);
            Optional<Booking> nextBookingOpt = bookingRepository.findAllByItem(item)
                    .stream()
                    .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now())).min(Comparator.comparing(Booking::getStart));
            Booking nextBooking = nextBookingOpt.orElse(null);
            itemDto
                    .setLastBooking(BookingMapper.INSTANCE.bookingToDto(lastBooking))
                    .setNextBooking(BookingMapper.INSTANCE.bookingToDto(nextBooking));
        }
        List<Comment> comments = commentRepository.findAllByItem(item);
        itemDto.setComments(commentMapper.toDto(comments));
        log.debug("Item {} was retrieved successfully from service {}", item, this.getClass());
        return itemDto;
    }

    @Override
    public Item update(Long ownerId, Item item) {
        log.debug("Update item request was received in service {}, with data {}", this.getClass(), item.toString());
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserDoesNotExistException(ownerId));
        item.setOwner(owner);
        Item updatedItem;
        if (itemRepository.existsById(item.getId())) {
            updatedItem = itemRepository.save(item);
        } else {
            throw new ItemDoesNotExistException(item.getId());
        }
        log.debug("Item {} was updated successfully in service {}", updatedItem, this.getClass());
        return updatedItem;
    }

    @Override
    public Item partialUpdate(Long ownerId, ItemPartialUpdateDto itemPartialUpdateDto) {
        log.debug("Update item request was received in service {}, with data {}",
                this.getClass(),
                itemPartialUpdateDto.toString());
        User user = userRepository.findById(ownerId).orElseThrow(() -> new UserDoesNotExistException(ownerId));
        Item updatedItem = itemRepository
                .findById(itemPartialUpdateDto.getId())
                .orElseThrow(() -> new ItemDoesNotExistException(itemPartialUpdateDto.getId()));
        if (!updatedItem.getOwner().equals(user)) {
            throw new ItemDoesNotExistException(itemPartialUpdateDto.getId());
        }
        if (itemPartialUpdateDto.getName() != null) {
            updatedItem.setName(itemPartialUpdateDto.getName());
        }
        if (itemPartialUpdateDto.getDescription() != null) {
            updatedItem.setDescription(itemPartialUpdateDto.getDescription());
        }
        if (itemPartialUpdateDto.getAvailable() != null) {
            updatedItem.setAvailable(itemPartialUpdateDto.getAvailable());
        }
        if (itemPartialUpdateDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemPartialUpdateDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestDoesNotExistException(itemPartialUpdateDto.getRequestId()));
            updatedItem.setRequest(itemRequest);
            updatedItem.setRequestId(itemRequest.getItemRequestId());
        }
        itemRepository.save(updatedItem);
        log.debug("Item {} was updated successfully in service {}", updatedItem, this.getClass());
        return updatedItem;
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        log.debug("Delete item request is received in controller {}, with id {}", this.getClass(), itemId);
        if (!userRepository.existsById(ownerId)) {
            throw new UserDoesNotExistException(ownerId);
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemDoesNotExistException(itemId));
        if (item.getOwner().getId() == ownerId) {
            itemRepository.delete(item);
        } else {
            throw new SecurityException("User has to be owner of the item");
        }
        log.debug("Item with id {} was deleted successfully in service {}", itemId, this.getClass());
    }

    @Override
    public List<ItemDto> getAll(Long ownerId) {
        log.debug("Get all items request is received in service {}", this.getClass());
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserDoesNotExistException(ownerId));
        List<Item> items = itemRepository.findAllByOwner(owner);
        List<Booking> bookings = bookingRepository.findAllByOwner(ownerId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            Optional<Booking> lastBookingOpt = bookings
                    .stream()
                    .filter(x -> x.getItem().equals(item))
                    .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                    .filter(x -> x.getEnd().isBefore(LocalDateTime.now())).max(Comparator.comparing(Booking::getEnd));
            Booking lastBooking = lastBookingOpt.orElse(null);
            Optional<Booking> nextBookingOpt = bookings
                    .stream()
                    .filter(x -> x.getItem().equals(item))
                    .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now())).min(Comparator.comparing(Booking::getStart));
            Booking nextBooking = nextBookingOpt.orElse(null);
            ItemDto itemDto = mapper.toItemDto(item);
            itemDto
                    .setLastBooking(BookingMapper.INSTANCE.bookingToDto(lastBooking))
                    .setNextBooking(BookingMapper.INSTANCE.bookingToDto(nextBooking));
            itemDtos.add(itemDto);
        }
        itemDtos.sort(Comparator.comparing(ItemDto::getId));
        return itemDtos;
    }

    @Override
    public List<Item> search(String text) {
        log.debug("Search items request is received in service {}", this.getClass());
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Set<Item> resultsInNames = itemRepository.findAllByNameContainsIgnoreCase(text);
        Set<Item> resultsInDescription = itemRepository.findAllByDescriptionContainsIgnoreCase(text);
        resultsInNames.addAll(resultsInDescription);
        return resultsInNames
                .stream()
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    public Item map(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemDoesNotExistException(itemId));
    }
}
