package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    private final TestEntityManager entityManager;

    @Test
    void findAllByItemId() {
        User owner = User.builder().name("Name").email("email@re.com").build();
        User commenter = User.builder().name("Commenter").email("email@tr.com").build();
        entityManager.persist(owner);
        entityManager.persist(commenter);
        Item item = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item);
        Comment comment1 = Comment.builder().text("comment1").item(item).author(commenter).build();
        Comment comment2 = Comment.builder().text("comment2").item(item).author(commenter).build();
        entityManager.persist(comment1);
        entityManager.persist(comment2);

        List<Comment> allComments = commentRepository.findAllByItemId(item.getId());

        assertThat(allComments.size()).isEqualTo(2);
        assertThat(allComments.get(0)).isEqualTo(comment1);
        assertThat(allComments.get(1)).isEqualTo(comment2);
        assertThat(allComments.get(0).getAuthor()).isEqualTo(commenter);
    }
}