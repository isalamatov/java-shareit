package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.CommentMapperImpl;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.interfaces.CommentRepository;
import ru.practicum.shareit.comments.interfaces.CommentService;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;
import ru.practicum.shareit.item.exceptions.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exceptions.ItemDoesNotExistException;
import ru.practicum.shareit.item.impl.ItemServiceImpl;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {
        ItemServiceImpl.class,
        ItemMapperImpl.class,
        UserMapperImpl.class,
        ItemRequestService.class,
        ItemRequestRepository.class,
        CommentMapperImpl.class,
        CommentService.class,
        BookingService.class,
        BookingRepository.class,
        CommentRepository.class,
        ItemRepository.class})
public class ItemServiceTests {
    @Autowired
    private ItemService itemService;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private CommentMapper commentMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private ItemRequestService itemRequestService;
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
    private Booking nextBooking;
    @Captor
    private ArgumentCaptor<Item> captor;

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
        nextBooking = new Booking(2L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                hammer,
                jane,
                BookingStatus.APPROVED);
    }

    @Test
    void createSuccessful() {
        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(itemRepository.existsById(any())).thenReturn(false);
        Mockito.when(itemRepository.save(any())).thenReturn(hammer);
        Item item = itemService.create(hammer);
        assertThat(item.getId(), equalTo(hammer.getId()));
        assertThat(item.getName(), equalTo(hammer.getName()));
    }

    @Test
    void createWithWrongUserShouldThrowException() {
        Mockito.when(userRepository.existsById(any())).thenReturn(false);
        assertThrows(UserDoesNotExistException.class, () -> itemService.create(hammer));
    }

    @Test
    void createWithWrongItemShouldThrowException() {
        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(itemRepository.existsById(any())).thenReturn(true);
        assertThrows(ItemAlreadyExistsException.class, () -> itemService.create(hammer));
    }

    @Test
    void getSuccefull() {
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(hammer));
        Mockito.when(bookingRepository.findAllByItem(any())).thenReturn(Collections.emptyList());
        Mockito.when(commentRepository.findAllByItem(any())).thenReturn(Collections.emptyList());
        ItemDto itemDto = itemService.get(hammer.getId(), john.getId());
        assertThat(itemDto.getName(), equalTo(hammer.getName()));
        assertThat(itemDto.getDescription(), equalTo(hammer.getDescription()));
    }

    @Test
    void getWithWrongItemIdShouldThrowException() {
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ItemDoesNotExistException.class, () -> itemService.get(hammer.getId(), john.getId()));
    }

    @Test
    void updateSuccessful() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(john));
        Mockito.when(itemRepository.existsById(any())).thenReturn(true);
        Mockito.when(itemRepository.save(any())).thenReturn(hammer);
        Item item = itemService.update(1L, hammer);
        assertThat(item.getId(), equalTo(hammer.getId()));
        assertThat(item.getName(), equalTo(hammer.getName()));
    }

    @Test
    void updateWrongItemIdShouldThrowException() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(john));
        Mockito.when(itemRepository.existsById(any())).thenReturn(false);
        assertThrows(ItemDoesNotExistException.class, () -> itemService.update(1L, hammer));
    }

    @Test
    void partialUpdateSuccessful() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(john));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(hammer));
        Mockito.when(itemRepository.save(any())).thenReturn(hammer);
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(hammerRequest));
        ItemPartialUpdateDto hammerPartialDto = new ItemPartialUpdateDto()
                .setId(hammerDto.getId())
                .setName(hammerDto.getName() + "updated")
                .setDescription(hammerDto.getDescription() + "updated")
                .setAvailable(false)
                .setRequestId(1L);
        itemService.partialUpdate(1L, hammerPartialDto);
        Mockito.verify(itemRepository).save(captor.capture());
        Item item = captor.getValue();
        assertThat(item.getId(), equalTo(hammerPartialDto.getId()));
        assertThat(item.getName(), equalTo(hammerPartialDto.getName()));
        assertThat(item.getDescription(), equalTo(hammerPartialDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(false));
        assertThat(item.getRequestId(), equalTo(hammerPartialDto.getRequestId()));
    }

    @Test
    void partialUpdateUserNotOwnerShouldThrowException() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(john));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(bulldozer));
        ItemPartialUpdateDto hammerPartialDto = new ItemPartialUpdateDto()
                .setId(hammerDto.getId())
                .setName(hammerDto.getName() + "updated")
                .setDescription(hammerDto.getDescription() + "updated")
                .setAvailable(false)
                .setRequestId(1L);
        assertThrows(ItemDoesNotExistException.class, () -> itemService.partialUpdate(1L, hammerPartialDto));
    }

    @Test
    void deleteSuccessful() {
        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(hammer));
        itemService.delete(1L, hammer.getId());
        Mockito.verify(itemRepository, Mockito.times(1)).delete(hammer);
    }


    @Test
    void deleteWithWrongUserShouldThrowException() {
        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(hammer));
        assertThrows(SecurityException.class, () -> itemService.delete(2L, hammer.getId()));
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(john));
        Mockito.when(itemRepository.findAllByOwner(any())).thenReturn(List.of(hammer));
        Mockito.when(bookingRepository.findAllByOwner(any())).thenReturn(List.of(lastBooking, nextBooking));
        Mockito.when(itemRepository.save(any())).thenReturn(hammer);
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(hammerRequest));
        List<ItemDto> itemDtos = itemService.getAll(john.getId());
        assertThat(itemDtos.get(0).getName(), equalTo(hammer.getName()));
        assertThat(itemDtos.get(0).getLastBooking().getId(), equalTo(lastBooking.getBookingId()));
        assertThat(itemDtos.get(0).getNextBooking().getId(), equalTo(nextBooking.getBookingId()));
    }

    @Test
    void search() {
        Set<Item> hammers = new HashSet<>();
        hammers.add(hammer);
        Set<Item> bulldozers = new HashSet<>();
        bulldozers.add(bulldozer);
        Mockito.when(itemRepository.findAllByNameContainsIgnoreCase(any())).thenReturn(hammers);
        Mockito.when(itemRepository.findAllByDescriptionContainsIgnoreCase(any())).thenReturn(bulldozers);
        List<Item> items = itemService.search("text");
        assertThat(items, hasSize(2));
    }

    @Test
    void searchBlankText() {
        List<Item> items = itemService.search("");
        assertThat(items, hasSize(0));
    }

    @Test
    void map() {
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(hammer));
        Item item = itemService.map(1L);
        assertThat(item.getId(), equalTo(hammer.getId()));
    }
}
