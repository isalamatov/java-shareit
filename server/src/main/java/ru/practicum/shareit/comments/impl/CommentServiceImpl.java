package ru.practicum.shareit.comments.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.item.exceptions.ItemDoesNotExistException;
import ru.practicum.shareit.item.exceptions.UserHasToBeBookerException;
import ru.practicum.shareit.comments.interfaces.CommentRepository;
import ru.practicum.shareit.comments.interfaces.CommentService;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final BookingService bookingService;
    private final CommentRepository commentRepository;

    private final ItemRepository itemRepository;

    @Override
    public Comment create(Comment comment, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemDoesNotExistException(itemId));
        List<Booking> bookings = bookingService.getAllByUserAndState(comment.getAuthor().getId(), State.PAST);
        if (bookings.stream().anyMatch(booking -> booking.getBooker().equals(comment.getAuthor()))) {
            comment.setItem(item);
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
        } else {
            throw new UserHasToBeBookerException("User has to book item prior to leaving comments on it");
        }
        return comment;
    }
}
