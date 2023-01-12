package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Class, describing request for specific item, issued by a specific user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private long itemRequestId;

    @Column(name = "description")
    private String description;

    @JoinColumn(name = "requestor_id")
    @ManyToOne
    private User requestor;

    @Column(name = "created")
    private LocalDateTime created;

    @JoinColumn(name = "request_id")
    @OneToMany
    private List<Item> items;
}
