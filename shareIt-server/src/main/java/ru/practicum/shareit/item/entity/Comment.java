package ru.practicum.shareit.item.entity;

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
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment_text", length = 1000, nullable = false)
    private String text;
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;
    @Column(name = "date_created", nullable = false)
    private LocalDateTime created;
}
