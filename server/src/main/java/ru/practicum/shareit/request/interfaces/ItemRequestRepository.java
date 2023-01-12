package ru.practicum.shareit.request.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>, QuerydslPredicateExecutor<ItemRequest> {
    List<ItemRequest> findAllByRequestor(User requestor);
}
