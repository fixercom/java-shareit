package ru.practicum.shareit.item.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(length = 500, nullable = false)
    private String description;
    @Column(nullable = false)
    private Boolean available;
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ItemRequest request;
}
