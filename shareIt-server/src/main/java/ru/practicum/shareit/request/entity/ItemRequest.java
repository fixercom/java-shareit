package ru.practicum.shareit.request.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 500, nullable = false)
    private String description;
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User requester;
    @Column(name = "date_created", nullable = false)
    private LocalDateTime created;
}
