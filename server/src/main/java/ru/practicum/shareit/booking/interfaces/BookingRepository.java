package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long>, QuerydslPredicateExecutor<ItemRequest> {
    List<Booking> findAllByBooker(User booker);

    @Query("SELECT new ru.practicum.shareit.booking.model.Booking(b.bookingId,b.start,b.end,b.item, b.booker, b.status)" +
            "FROM items AS i " +
            "LEFT JOIN bookings AS b ON b.item.id = i.id " +
            "LEFT JOIN users AS u ON b.bookingId = u.id " +
            "WHERE i.owner.id = ?1")
    List<Booking> findAllByOwner(Long ownerId);

    List<Booking> findAllByItem(Item item);
}
