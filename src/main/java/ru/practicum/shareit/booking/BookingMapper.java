package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        uses = {ItemService.class, UserService.class, ItemMapper.class})
@AllArgsConstructor
public abstract class BookingMapper {

    public static final BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "bookerId", target = "booker")
    @Mapping(source = "itemId", target = "item")
    @Mapping(target = "status", defaultValue = "WAITING")
    public abstract Booking dtoToBooking(BookingDto bookingDto);

    @Mapping(target = "item.requestId", source = "item.request.itemRequestId")
    @Mapping(target = "id", source = "bookingId")
    @Mapping(target = "bookerId", source = "booker.id")
    @Mapping(target = "itemId", source = "item.id")
    public abstract BookingDto bookingToDto(Booking booking);

    public abstract List<BookingDto> bookingToDto(Collection<Booking> booking);
}
