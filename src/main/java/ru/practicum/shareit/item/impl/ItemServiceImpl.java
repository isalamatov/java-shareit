package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;
import ru.practicum.shareit.item.exceptions.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exceptions.ItemDoesNotExistException;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
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
            itemDto = enrichItemDto(itemDto, item);
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
        if (!userRepository.existsById(ownerId)) {
            throw new UserDoesNotExistException(ownerId);
        }
        Item updatedItem = itemRepository
                .findById(itemPartialUpdateDto.getId())
                .orElseThrow(() -> new ItemDoesNotExistException(itemPartialUpdateDto.getId()));
        if (updatedItem.getOwner().getId() != ownerId) {
            throw new ItemDoesNotExistException(itemPartialUpdateDto.getId());
        }
        Field[] fields = itemPartialUpdateDto.getClass().getDeclaredFields();
        Field[] itemFields = updatedItem.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(itemPartialUpdateDto) != null) {
                    Field userField = Arrays.stream(itemFields)
                            .sequential()
                            .filter(x -> x.getName().equalsIgnoreCase(field.getName()))
                            .findFirst().orElseThrow(NoSuchFieldException::new);
                    userField.setAccessible(true);
                    userField.set(updatedItem, field.get(itemPartialUpdateDto));
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                log.warn("Mapping partial DTO fields error with message {}", e.getMessage());
            }
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
        itemRepository.delete(item);
        log.debug("Item with id {} was deleted successfully in service {}", itemId, this.getClass());
    }

    @Override
    public List<ItemDto> getAll(Long ownerId) {
        log.debug("Get all items request is received in service {}", this.getClass());
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserDoesNotExistException(ownerId));
        List<Item> items = itemRepository.findAllByOwner(owner);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = mapper.toItemDto(item);
            itemDto = enrichItemDto(itemDto, item);
            List<Comment> comments = commentRepository.findAllByItem(item);
            itemDto.setComments(commentMapper.toDto(comments));
            itemDtos.add(itemDto);
        }
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

    private ItemDto enrichItemDto(ItemDto itemDto, Item item) {
        Optional<Booking> lastBooking = bookingRepository.findAllByItem(item)
                .stream()
                .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now())).max(Comparator.comparing(Booking::getEnd));
        BookingDto lastBookingDto = lastBooking.map(bookingMapper::bookingToDto).orElse(null);
        Optional<Booking> nextBooking = bookingRepository.findAllByItem(item)
                .stream()
                .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                .filter(x -> x.getStart().isAfter(LocalDateTime.now())).min(Comparator.comparing(Booking::getStart));
        BookingDto nextBookingDto = nextBooking.map(bookingMapper::bookingToDto).orElse(null);
        itemDto.setLastBooking(lastBookingDto).setNextBooking(nextBookingDto);
        return itemDto;
    }
}
