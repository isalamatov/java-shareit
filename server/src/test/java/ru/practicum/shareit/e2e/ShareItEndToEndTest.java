package ru.practicum.shareit.e2e;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = ShareItApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback(value = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShareItEndToEndTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private BookingRepository bookingRepository;
    private User john;
    private UserDto johnDto;
    private User jane;
    private UserDto janeDto;
    private ItemRequest hammerRequest;
    private ItemRequestDto hammerRequestDto;
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
    private HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void setEnvironment() {
        john = new User(1, "John", "john@doe.com");
        johnDto = userMapper.toUserDto(john);
        jane = new User(2, "Jane", "jane@doe.com");
        janeDto = userMapper.toUserDto(jane);
        hammerRequest = new ItemRequest(
                1L,
                "I need hammer",
                jane, LocalDateTime.now(),
                null);
        hammerRequestDto = itemRequestMapper.toDto(hammerRequest);
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
        headers.add("X-Sharer-User-Id", "1");
    }

    @SneakyThrows
    @Test
    @Order(1)
    void createUserJohn() {
        ResponseEntity<UserDto> response = restTemplate.postForEntity("/users", johnDto, UserDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getName(), equalTo(johnDto.getName()));
    }

    @SneakyThrows
    @Test
    @Order(2)
    void createUserJane() {
        ResponseEntity<UserDto> response = restTemplate.postForEntity("/users", janeDto, UserDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getName(), equalTo(janeDto.getName()));
    }

    @Test
    @Order(3)
    void createItemHammer() {
        HttpEntity<ItemDto> itemDtoHttpEntity = new HttpEntity<>(hammerDto, headers);
        ResponseEntity<ItemDto> response = restTemplate.exchange("/items", HttpMethod.POST, itemDtoHttpEntity, ItemDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getName(), equalTo(hammerDto.getName()));
    }

    @Test
    @Order(4)
    void createItemBulldozer() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<ItemDto> itemDtoHttpEntity = new HttpEntity<>(bulldozerDto, headers);
        ResponseEntity<ItemDto> response = restTemplate.exchange("/items", HttpMethod.POST, itemDtoHttpEntity, ItemDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getName(), equalTo(bulldozerDto.getName()));
    }

    @Test
    @Order(5)
    void createBookingHammer() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(nextBookingDto, headers);
        ResponseEntity<BookingDto> response = restTemplate.exchange("/bookings", HttpMethod.POST, bookingDtoHttpEntity, BookingDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getItemId(), equalTo(hammer.getId()));
    }

    @SneakyThrows
    @Test
    @Order(6)
    void getBookingHammer() {
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<BookingDto> response = restTemplate.exchange("/bookings/1", HttpMethod.GET, bookingDtoHttpEntity, BookingDto.class);
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getItemId(), equalTo(hammer.getId()));
    }

    @SneakyThrows
    @Test
    @Order(7)
    void setBookingApproved() {
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<BookingDto> response = restTemplate.exchange("/bookings/1?approved=TRUE", HttpMethod.PATCH, bookingDtoHttpEntity, BookingDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @SneakyThrows
    @Test
    @Order(8)
    void getBookingByUserAndState() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange("/bookings", HttpMethod.GET, bookingDtoHttpEntity, new ParameterizedTypeReference<List<BookingDto>>() {
        });
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().get(0).getId(), equalTo(hammer.getId()));
    }

    @SneakyThrows
    @Test
    @Order(9)
    void getBookingByUserAndStateCurrent() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange("/bookings?state=CURRENT", HttpMethod.GET, bookingDtoHttpEntity, new ParameterizedTypeReference<List<BookingDto>>() {
        });
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), equalTo(0));
    }

    @SneakyThrows
    @Test
    @Order(10)
    void getBookingByUserAndStateWaiting() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange("/bookings?state=WAITING", HttpMethod.GET, bookingDtoHttpEntity, new ParameterizedTypeReference<List<BookingDto>>() {
        });
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), equalTo(0));
    }

    @SneakyThrows
    @Test
    @Order(11)
    void getBookingByUserAndStateRejected() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange("/bookings?state=REJECTED", HttpMethod.GET, bookingDtoHttpEntity, new ParameterizedTypeReference<List<BookingDto>>() {
        });
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), equalTo(0));
    }

    @SneakyThrows
    @Test
    @Order(12)
    void getBookingByUserAndStatePast() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange("/bookings?state=PAST", HttpMethod.GET, bookingDtoHttpEntity, new ParameterizedTypeReference<List<BookingDto>>() {
        });
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), equalTo(0));
    }

    @SneakyThrows
    @Test
    @Order(13)
    void getBookingByUserAndStateFuture() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<BookingDto> bookingDtoHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange("/bookings?state=FUTURE", HttpMethod.GET, bookingDtoHttpEntity, new ParameterizedTypeReference<List<BookingDto>>() {
        });
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), equalTo(1));
    }

    @SneakyThrows
    @Test
    @Order(14)
    void addComment() {
        bookingRepository.save(lastBooking);
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<CommentDto> bookingDtoHttpEntity = new HttpEntity<>(commentDto, headers);
        ResponseEntity<CommentDto> response = restTemplate.exchange("/items/1/comment", HttpMethod.POST, bookingDtoHttpEntity, CommentDto.class);
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getAuthorName(), equalTo(jane.getName()));
    }

    @Test
    @Order(15)
    void createItemRequestHammer() {
        HttpEntity<ItemRequestDto> itemRequestHttpEntity = new HttpEntity<>(hammerRequestDto, headers);
        ResponseEntity<ItemRequestDto> response = restTemplate.exchange("/requests", HttpMethod.POST, itemRequestHttpEntity, ItemRequestDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getDescription(), equalTo(hammerRequestDto.getDescription()));
    }

    @Test
    @Order(16)
    void getAllItemRequestsByRequestor() {
        HttpEntity<ItemRequestDto> itemRequestHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<ItemRequestDto>> response = restTemplate.exchange("/requests", HttpMethod.GET, itemRequestHttpEntity, new ParameterizedTypeReference<List<ItemRequestDto>>() {
        });
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().get(0).getDescription(), equalTo(hammerRequestDto.getDescription()));
    }

    @Test
    @Order(17)
    void getAllItemRequests() {
        headers.clear();
        headers.add("X-Sharer-User-Id", "2");
        HttpEntity<ItemRequestDto> itemRequestHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<ItemRequestDto>> response = restTemplate.exchange("/requests/all", HttpMethod.GET, itemRequestHttpEntity, new ParameterizedTypeReference<List<ItemRequestDto>>() {
        });
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().get(0).getDescription(), equalTo(hammerRequestDto.getDescription()));
    }

    @Test
    @Order(18)
    void getItemRequestById() {
        HttpEntity<ItemRequestDto> itemRequestHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<ItemRequestDto> response = restTemplate.exchange("/requests/1", HttpMethod.GET, itemRequestHttpEntity, ItemRequestDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getDescription(), equalTo(hammerRequestDto.getDescription()));
    }
}
