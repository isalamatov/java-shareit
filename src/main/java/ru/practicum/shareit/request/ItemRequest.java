package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

    @NotNull
    @Column(name = "description")
    private String description;

    @NotNull
    @JoinColumn(name = "requestor_id")
    @OneToOne
    private User requestor;

    @Column(name = "created")
    private LocalDateTime created;
}
