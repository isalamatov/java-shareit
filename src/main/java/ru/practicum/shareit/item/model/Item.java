package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Class, describing shared item or device,
 * which is used in Java code and RDBMS
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @NotBlank(message = "Item name should not be blank")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Item description should not be blank")
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "available")
    private Boolean available;

    @JoinColumn(name = "owner_id")
    @OneToOne
    private User owner;

    @JoinColumn(name = "request_id")
    @OneToOne
    private ItemRequest request;
}
