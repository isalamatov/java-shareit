package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.exceptions.ItemDoesNotExistException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class BookingMapper {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    protected BookingMapper(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public Booking dtoToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        User user = userRepository.findById(bookingDto.getBookerId())
                .orElseThrow(() -> new UserDoesNotExistException(bookingDto.getBookerId()));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemDoesNotExistException(bookingDto.getItemId()));
        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), formatter);
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now()) || end.isBefore(start)) {
            throw new IllegalArgumentException("Start and end time should be correct value");
        }
        return booking
                .setStart(start)
                .setEnd(end)
                .setItem(item).setBooker(user)
                .setStatus(BookingStatus.WAITING);
    }

    public BookingDto bookingToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        return bookingDto
                .setId(booking.getBookingId())
                .setStart(booking.getStart().format(formatter))
                .setEnd(booking.getEnd().format(formatter))
                .setItemId(booking.getItem().getId())
                .setBookerId(booking.getBooker().getId())
                .setItem(booking.getItem())
                .setBooker(booking.getBooker())
                .setStatus(booking.getStatus());
    }

    public List<BookingDto> bookingToDto(Collection<Booking> bookings) {
        if (bookings == null) {
            return null;
        }

        List<BookingDto> list = new ArrayList<>(bookings.size());
        for (Booking booking : bookings) {
            list.add(bookingToDto(booking));
        }

        return list;
    }

    public List<Booking> dtoToBooking(Collection<BookingDto> bookingDtos) {
        if (bookingDtos == null) {
            return null;
        }

        List<Booking> list = new ArrayList<>(bookingDtos.size());
        for (BookingDto bookingDto : bookingDtos) {
            list.add(dtoToBooking(bookingDto));
        }

        return list;
    }
}
