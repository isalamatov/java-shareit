package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper mapper;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody BookingDto bookingDto) {
        log.debug("Create booking request was received in controller {} with data {}",
                this.getClass(),
                bookingDto.toString());
        Booking booking = mapper.dtoToBooking(bookingDto.setBookerId(userId));
        Booking createdBooking = bookingService.create(booking);
        log.debug("Booking {} was created successfully in controller {}", createdBooking.toString(), this.getClass());
        return mapper.bookingToDto(createdBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.debug("Get booking request was received in controller {} with data {}",
                this.getClass(),
                bookingId);
        Booking booking = bookingService.get(userId, bookingId);
        log.debug("Booking {} was retrieved successfully in controller {}", booking, this.getClass());
        return mapper.bookingToDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id")
                                     Long ownerId,
                                     @PathVariable
                                     Long bookingId,
                                     @RequestParam("approved")
                                     Boolean approved) {
        log.debug("Booking approval request was received in controller {} with data {}",
                this.getClass(),
                bookingId);
        Booking booking = bookingService.approve(ownerId, bookingId, approved);
        log.debug("Booking {} was approved successfully in controller {}", bookingId, this.getClass());
        return mapper.bookingToDto(booking);
    }

    @GetMapping
    public List<BookingDto> getAllByUserAndState(@RequestHeader("X-Sharer-User-Id")
                                                 Long userId,
                                                 @RequestParam(defaultValue = "ALL")
                                                 State state) {
        log.debug("Get booking requests with params was received in controller {} with data {}",
                this.getClass(),
                userId);
        List<Booking> bookings = bookingService.getAllByUserAndState(userId, state);
        log.debug("Bookings was retrieved successfully in controller {}", this.getClass());
        return mapper.bookingToDto(bookings);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerAndState(@RequestHeader("X-Sharer-User-Id")
                                                  Long userId,
                                                  @RequestParam(defaultValue = "ALL")
                                                  State state) {
        log.debug("Get booking requests with params was received in controller {} with data {}",
                this.getClass(),
                userId);
        List<Booking> bookings = bookingService.getAllByOwnerAndState(userId, state);
        log.debug("Bookings was retrieved successfully in controller {}", this.getClass());
        return mapper.bookingToDto(bookings);
    }
}
