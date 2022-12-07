package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class, describing booking entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private long bookingId;

    @Column(name = "start_time")
    private LocalDateTime start;

    @Column(name = "end_time")
    private LocalDateTime end;

    @JoinColumn(name = "item_id")
    @OneToOne
    private Item item;

    @JoinColumn(name = "booker_id")
    @OneToOne
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
