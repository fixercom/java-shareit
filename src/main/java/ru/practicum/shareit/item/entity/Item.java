package ru.practicum.shareit.item.entity;

import lombok.*;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import java.util.Objects;

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
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    @Column(name = "description", length = 500, nullable = false)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private User owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description) &&
                Objects.equals(available, item.available) &&
                Objects.equals(owner, item.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner);
    }
}
