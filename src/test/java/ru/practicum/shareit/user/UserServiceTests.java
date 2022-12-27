package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserPartialUpdateDto;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.impl.UserServiceImpl;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {UserServiceImpl.class})
public class UserServiceTests {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    private User john = new User(1, "John", "john@doe.com");
    private User jane = new User(2, "Jane", "jane@doe.com");

    @Test
    void createUser() {
        Mockito.when(userRepository.save(any())).thenReturn(john);
        User user = userService.create(john);
        assertThat(user, equalTo(user));
    }

    @Test
    void get() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(john));
        User user = userService.get(john.getId());
        assertThat(user, equalTo(user));
    }

    @Test
    void getByWrongId() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserDoesNotExistException.class, () -> userService.get(john.getId()));
    }

    @Test
    void update() {
        Mockito.when(userRepository.existsById(john.getId())).thenReturn(true);
        Mockito.when(userRepository.save(any())).thenReturn(john);
        User user = userService.update(john);
        assertThat(user, equalTo(user));
    }

    @Test
    void updateNonExistentUser() {
        Mockito.when(userRepository.existsById(john.getId())).thenReturn(false);
        assertThrows(UserDoesNotExistException.class, () -> userService.update(john));
    }

    @Test
    void partialUpdate() {
        Mockito.when(userRepository.findById(john.getId())).thenReturn(Optional.ofNullable(john));
        Mockito.when(userRepository.save(any())).thenReturn(john);
        UserPartialUpdateDto updateDto = new UserPartialUpdateDto().setId(john.getId()).setName(john.getName());
        User user = userService.partialUpdate(updateDto);
        assertThat(user.getName(), equalTo(updateDto.getName()));
    }

    @Test
    void partialUpdateNonExistentUser() {
        Mockito.when(userRepository.findById(john.getId())).thenReturn(Optional.empty());
        UserPartialUpdateDto userPartialUpdateDto = new UserPartialUpdateDto().setId(john.getId());
        assertThrows(UserDoesNotExistException.class, () -> userService.partialUpdate(userPartialUpdateDto));
    }

    @Test
    void partialUpdateWithDuplicateEmail() {
        Mockito.when(userRepository.existsByEmailIgnoreCase(john.getEmail())).thenReturn(true);
        UserPartialUpdateDto userPartialUpdateDto = new UserPartialUpdateDto().setEmail(jane.getEmail());
        assertThrows(UserDoesNotExistException.class, () -> userService.partialUpdate(userPartialUpdateDto));
    }

    @Test
    void delete() {
        Mockito.doNothing().when(userRepository).delete(john);
        Mockito.when(userRepository.findById(john.getId())).thenReturn(Optional.ofNullable(john));
        userService.delete(john.getId());
        Mockito.verify(userRepository, Mockito.times(1)).delete(john);
    }

    @Test
    void deleteNonExistentUser() {
        Mockito.when(userRepository.findById(john.getId())).thenReturn(Optional.empty());
        assertThrows(UserDoesNotExistException.class, () -> userService.delete(john.getId()));
    }
}
