package ru.practicum.shareit.comments;

import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/**
 * Class, describing request for specific item, issued by a specific user.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "text")
    private String text;

    @JoinColumn(name = "item_id")
    @OneToOne
    private Item item;

    @JoinColumn(name = "author_id")
    @OneToOne
    private User author;
}
