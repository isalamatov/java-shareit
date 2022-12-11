package ru.practicum.shareit.comments.model;

import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "comments")
@Accessors(chain = true)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;
    @Column(name = "text")
    private String text;
    @JoinColumn(name = "item_id")
    @OneToOne
    private Item item;
    @JoinColumn(name = "author_id")
    @OneToOne
    private User author;
    @Column(name = "created")
    private LocalDateTime created;
}
