package ru.practicum.shareit.item.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Set<Item> findAllByDescriptionContainsIgnoreCase(String text);

    Set<Item> findAllByNameContainsIgnoreCase(String text);

    List<Item> findAllByOwner(User owner);
}
