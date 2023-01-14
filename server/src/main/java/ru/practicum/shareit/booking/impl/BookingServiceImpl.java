package ru.practicum.shareit.booking.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.exceptions.BookingDoesNotExistsException;
import ru.practicum.shareit.booking.exceptions.BookingStatusChangeException;
import ru.practicum.shareit.booking.exceptions.ItemUnavailableException;
import ru.practicum.shareit.booking.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    public Booking create(Booking booking) {
        log.debug("Create booking request was received in service {}, with data {}", this.getClass(), booking);
        if (!booking.getItem().getAvailable()) {
            throw new ItemUnavailableException(booking.getItem().getId());
        }
        if (booking.getBooker().equals(booking.getItem().getOwner())) {
            throw new SecurityException("Owner can't book his own items");
        }
        log.debug("Create booking request was processed in service {}, with data {}", this.getClass(), booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking get(Long userId, Long bookingId) {
        log.debug("Get booking request was received in service {}, with data {}", this.getClass(), bookingId);
        User user = userService.get(userId);
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new BookingDoesNotExistsException(bookingId));
        if (user.equals(booking.getBooker()) || user.equals(booking.getItem().getOwner())) {
            log.debug("Get booking request was processed in service {}, with data {}", this.getClass(), bookingId);
            return booking;
        } else {
            throw new SecurityException("Unauthorized request. User has to be either owner or requestor");
        }
    }

    @Override
    public Booking approve(Long ownerId, Long bookingId, Boolean approved) {
        log.debug("Approve booking request was received in service {}, with data {}", this.getClass(), bookingId);
        User user = userService.get(ownerId);
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new BookingDoesNotExistsException(bookingId));
        if (!user.equals(booking.getItem().getOwner())) {
            throw new SecurityException("Unauthorized request. User has to be owner.");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingStatusChangeException("Can't change status of booking, that has been already approved");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        log.debug("Approve booking request was processed in service {}, with data {}", this.getClass(), bookingId);
        return booking;
    }

    @Override
    public List<Booking> getAllByUserAndState(Long userId, State state, Integer from, Integer size) {
        log.debug("Get booking request by user was received in service {}, with data {}", this.getClass(), userId);
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Offset and limit should be correct values");
        }
        User user = userService.get(userId);
        List<Booking> bookings = filterByState(bookingRepository.findAllByBooker(user), state);
        bookings = bookings.stream().skip(from).limit(size).collect(Collectors.toList());
        log.debug("Get booking request by user was processed in service {}, with data {}", this.getClass(), userId);
        return bookings;
    }

    @Override
    public List<Booking> getAllByUserAndState(Long userId, State state) {
        log.debug("Get booking request by user was received in service {}, with data {}", this.getClass(), userId);
        User user = userService.get(userId);
        List<Booking> bookings = filterByState(bookingRepository.findAllByBooker(user), state);
        log.debug("Get booking request by user was processed in service {}, with data {}", this.getClass(), userId);
        return bookings;
    }

    @Override
    public List<Booking> getAllByOwnerAndState(Long userId, State state, Integer from, Integer size) {
        log.debug("Get booking request by owner was received in service {}, with data {}", this.getClass(), userId);
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Offset and limit should be correct values");
        }
        User user = userService.get(userId);
        List<Booking> bookings = filterByState(bookingRepository.findAllByOwner(user.getId()), state);
        bookings = bookings.stream().skip(from).limit(size).collect(Collectors.toList());
        log.debug("Get booking request by owner was processed in service {}, with data {}", this.getClass(), userId);
        return bookings;
    }

    private List<Booking> filterByState(List<Booking> bookings, State state) {
        switch (state) {
            case ALL: {
                return bookings
                        .stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case WAITING: {
                return bookings
                        .stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case REJECTED: {
                return bookings
                        .stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case CURRENT: {
                return bookings
                        .stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case PAST: {
                return bookings
                        .stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
            }
            case FUTURE: {
                return bookings
                        .stream()
                        .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED))
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
            }
            default: {
                throw new UnsupportedStatusException(state.toString());
            }
        }
    }
}
