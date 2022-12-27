package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.exceptions.BookingDoesNotExistsException;
import ru.practicum.shareit.booking.exceptions.BookingStatusChangeException;
import ru.practicum.shareit.booking.exceptions.ItemUnavailableException;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.CommentMapperImpl;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.interfaces.CommentRepository;
import ru.practicum.shareit.comments.interfaces.CommentService;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        BookingController.class,
        ItemMapperImpl.class,
        UserMapperImpl.class,
        ItemRequestService.class,
        ItemRequestRepository.class,
        CommentMapperImpl.class,
        CommentService.class,
        BookingService.class,
        BookingMapper.class,
        BookingRepository.class,
        CommentRepository.class,
        ItemRepository.class
})
public class BookingControllerTests {
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private ItemRepository itemRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BookingMapper bookingMapper;
    private User john;
    private User jane;
    private ItemRequest hammerRequest;
    private Item hammer;
    private ItemDto hammerDto;
    private Item bulldozer;
    private ItemDto bulldozerDto;
    private Comment comment;
    private CommentDto commentDto;
    private List<Item> devices = new ArrayList<>();
    private List<ItemDto> devicesDto = new ArrayList<>();
    private Booking lastBooking;
    private BookingDto lastBookingDto;
    private BookingDto nextBookingDto;
    private Booking nextBooking;
    @Captor
    private ArgumentCaptor<BookingDto> captor;

    @BeforeEach
    void setEnvironment() {
        john = new User(1, "John", "john@doe.com");
        jane = new User(2, "Jane", "jane@doe.com");
        hammerRequest = new ItemRequest(
                1L,
                "I need hammer",
                jane, LocalDateTime.now(),
                null);
        hammer = new Item(1L,
                "Hammer",
                "Hand hammer",
                true,
                john,
                null,
                null);
        hammerDto = itemMapper.toItemDto(hammer);
        bulldozer = new Item(2L,
                "Bulldozer",
                "Big machine",
                true,
                jane,
                null,
                null);
        bulldozerDto = itemMapper.toItemDto(bulldozer);
        devices.addAll(List.of(hammer, bulldozer));
        devicesDto.addAll(List.of(hammerDto, bulldozerDto));
        comment = new Comment(1L, "First comment", hammer, jane, LocalDateTime.now());
        commentDto = commentMapper.toDto(comment);
        lastBooking = new Booking(1L,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                hammer,
                jane,
                BookingStatus.APPROVED);
        lastBookingDto = bookingMapper.bookingToDto(lastBooking);
        nextBooking = new Booking(2L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                hammer,
                jane,
                BookingStatus.WAITING);
        nextBookingDto = bookingMapper.bookingToDto(nextBooking);
    }

    @Test
    void create() throws Exception {
        Mockito.when(bookingService.create(any())).thenReturn(nextBooking.setStatus(BookingStatus.WAITING));
        Mockito.doNothing().when(bookingService).validate(any());
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(nextBookingDto.setStatus(null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(nextBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    /**
     * Handler testing to reach coverage.
     */

    @Test
    void createThrowsException() throws Exception {
        Mockito.when(bookingService.create(any())).thenThrow(new SecurityException(""));
        Mockito.doNothing().when(bookingService).validate(any());
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(nextBookingDto.setStatus(null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createThrowsExceptionBooking() throws Exception {
        Mockito.when(bookingService.create(any())).thenThrow(new BookingDoesNotExistsException(1L));
        Mockito.doNothing().when(bookingService).validate(any());
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(nextBookingDto.setStatus(null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createThrowsSecurityException() throws Exception {
        Mockito.when(bookingService.create(any())).thenThrow(new SecurityException(""));
        Mockito.doNothing().when(bookingService).validate(any());
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(nextBookingDto.setStatus(null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createThrowsItemUnavailableException() throws Exception {
        Mockito.when(bookingService.create(any())).thenThrow(new ItemUnavailableException(1L));
        Mockito.doNothing().when(bookingService).validate(any());
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(nextBookingDto.setStatus(null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createThrowsItemIllegalArgumentException() throws Exception {
        Mockito.when(bookingService.create(any())).thenThrow(new IllegalArgumentException());
        Mockito.doNothing().when(bookingService).validate(any());
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(nextBookingDto.setStatus(null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createThrowsBookingStatusChangeException() throws Exception {
        Mockito.when(bookingService.create(any())).thenThrow(new BookingStatusChangeException(""));
        Mockito.doNothing().when(bookingService).validate(any());
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(nextBookingDto.setStatus(null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError());
    }
}
