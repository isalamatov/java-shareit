package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, UserMapperImpl.class})
public class UserControllerTests {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private User john = new User(1, "John", "john@doe.com");
    private User jane = new User(2, "Jane", "jane@doe.com");

    @Test
    void getUser() throws Exception {
        Mockito.when(userService.get(any())).thenReturn(john);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(john.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(john.getName())))
                .andExpect(jsonPath("$.email", is(john.getEmail())));
    }

    @Test
    void getUnknownUserThrowsException() throws Exception {
        Mockito.when(userService.get(any())).thenThrow(new UserDoesNotExistException(1));

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getAllUsers() throws Exception {
        Mockito.when(userService.getAll()).thenReturn(List.of(john, jane));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[1]['id']", is(jane.getId()), Long.class))
                .andExpect(jsonPath("$[1]['name']", is(jane.getName())))
                .andExpect(jsonPath("$[1]['email']", is(jane.getEmail())));
    }

    @Test
    void createUser() throws Exception {
        Mockito.when(userService.create(any())).thenReturn(john);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(john))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(john.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(john.getName())))
                .andExpect(jsonPath("$.email", is(john.getEmail())));
    }

    @Test
    void createUserWithoutEmail() throws Exception {
        john.setEmail(null);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(john))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateUser() throws Exception {
        Mockito.when(userService.update(any())).thenReturn(john);

        mvc.perform(put("/users/1")
                        .content(mapper.writeValueAsString(john))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(john.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(john.getName())))
                .andExpect(jsonPath("$.email", is(john.getEmail())));
    }

    @Test
    void updateUserPartial() throws Exception {
        Mockito.when(userService.partialUpdate(any())).thenReturn(john);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(john))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(john.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(john.getName())))
                .andExpect(jsonPath("$.email", is(john.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Handler testing to reach coverage.
     */
    @Test
    void getThrowsUserDoesAlreadyExistException() throws Exception {
        Mockito.when(userService.get(any())).thenThrow(new UserAlreadyExistsException(""));

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
