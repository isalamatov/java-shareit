package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class ItemRepositoryTests {
    @Autowired
    private ItemRepository itemRepository;
    private User john;
    private User jane;
    private Item hammer;
    private Item bulldozer;
    private List<Item> devices = new ArrayList<>();
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setEnvironment() {
        john = new User(1, "John", "john@doe.com");
        jane = new User(2, "Jane", "jane@doe.com");
        hammer = new Item(1L,
                "Hammer",
                "Hand hammer",
                true,
                john,
                null,
                null);
        bulldozer = new Item(2L,
                "Bulldozer",
                "Big machine",
                true,
                jane,
                null,
                null);
        devices.addAll(List.of(hammer, bulldozer));
        userRepository.save(john);
        userRepository.save(jane);
        itemRepository.save(hammer);
        itemRepository.save(bulldozer);
    }

    @Test
    void findAllByDescriptionContainsIgnoreCase() {
        Set<Item> items = itemRepository.findAllByDescriptionContainsIgnoreCase("hand");
        assertThat(items, hasSize(1));
        assertThat(new ArrayList<>(items).get(0).getId(), equalTo(hammer.getId()));
    }

    @Test
    void findAllByNameContainsIgnoreCase() {
        Set<Item> items = itemRepository.findAllByNameContainsIgnoreCase("bull");
        assertThat(items, hasSize(1));
        assertThat(new ArrayList<>(items).get(0).getId(), equalTo(bulldozer.getId()));
    }

    @Test
    void findAllByOwner() {
        List<Item> items = itemRepository.findAllByOwner(john);
        assertThat(items, hasSize(1));
        assertThat(items.get(0).getId(), equalTo(hammer.getId()));
    }
}
