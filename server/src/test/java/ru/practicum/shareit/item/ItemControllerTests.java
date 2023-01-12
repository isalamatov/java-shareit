package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
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
import ru.practicum.shareit.item.exceptions.UserHasToBeBookerException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        ItemController.class,
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
public class ItemControllerTests {
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
    }

    @Test
    void create() throws Exception {
        Mockito.when(itemService.create(any())).thenReturn(hammer);
        Mockito.when(userService.get(john.getId())).thenReturn(john);
        Mockito.when(userService.get(jane.getId())).thenReturn(jane);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(hammer.getName())));
    }

    @Test
    void createWithWrongId() throws Exception {
        Mockito.when(userService.get(any())).thenThrow(new UserDoesNotExistException(john.getId()));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createWithWrongItem() throws Exception {
        Mockito.when(itemService.create(any())).thenThrow(new ItemAlreadyExistsException(hammer.getId()));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createWithWrongBooker() throws Exception {
        Mockito.when(itemService.create(any())).thenThrow(new UserHasToBeBookerException(john.getEmail()));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getOne() throws Exception {
        Mockito.when(itemService.get(any(), any())).thenReturn(hammerDto);
        mvc.perform(get("/items/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(hammer.getName())));
    }

    @Test
    void update() throws Exception {
        Mockito.when(itemService.update(any(), any())).thenReturn(hammer);
        Mockito.when(userService.get(john.getId())).thenReturn(john);
        Mockito.when(userService.get(jane.getId())).thenReturn(jane);
        mvc.perform(put("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(hammer.getName())));
    }

    @Test
    void partialUpdate() throws Exception {
        ItemPartialUpdateDto hammerPartial = new ItemPartialUpdateDto().setId(hammer.getId()).setName(hammer.getName());
        Mockito.when(itemService.partialUpdate(any(), any())).thenReturn(hammer);
        Mockito.when(userService.get(john.getId())).thenReturn(john);
        Mockito.when(userService.get(jane.getId())).thenReturn(jane);
        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(hammerPartial))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(hammer.getName())));
    }

    @Test
    void deleteOne() throws Exception {
        Mockito.doNothing().when(itemService).delete(any(), any());
        mvc.perform(delete("/items/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        Mockito.when(itemService.getAll(any())).thenReturn(devicesDto);
        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0]['id']", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$[1]['name']", is(bulldozer.getName())));
    }

    @Test
    void search() throws Exception {
        Mockito.when(itemService.search(any())).thenReturn(devices);
        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "anytext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0]['id']", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$[1]['name']", is(bulldozer.getName())));
    }

    @Test
    void addComment() throws Exception {
        Mockito.when(commentService.create(any(), any())).thenReturn(comment);
        Mockito.when(userService.get(any())).thenReturn(jane);
        mvc.perform(post("/items//{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['id']", is(comment.getCommentId()), Long.class))
                .andExpect(jsonPath("$['text']", is(comment.getText())));
    }

    @Test
    void createThrowsItemDoesNotExistException() throws Exception {
        Mockito.when(itemService.create(any())).thenThrow(new ItemDoesNotExistException(1L));
        Mockito.when(userService.get(john.getId())).thenReturn(john);
        Mockito.when(userService.get(jane.getId())).thenReturn(jane);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createThrowsItemAlreadyExistsException() throws Exception {
        Mockito.when(itemService.create(any())).thenThrow(new ItemAlreadyExistsException(1L));
        Mockito.when(userService.get(john.getId())).thenReturn(john);
        Mockito.when(userService.get(jane.getId())).thenReturn(jane);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createThrowsUserHasToBeBookerException() throws Exception {
        Mockito.when(itemService.create(any())).thenThrow(new UserHasToBeBookerException(""));
        Mockito.when(userService.get(john.getId())).thenReturn(john);
        Mockito.when(userService.get(jane.getId())).thenReturn(jane);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }
}
