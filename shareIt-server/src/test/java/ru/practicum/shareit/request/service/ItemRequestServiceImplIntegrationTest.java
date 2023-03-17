package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemRequestServiceImplIntegrationTest {
    private final ItemRequestServiceImpl itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void getAllOwnItemRequests() {
        User itemRequestOwner = createUser("1");
        User anotherUser = createUser("2");
        userRepository.saveAll(List.of(itemRequestOwner, anotherUser));
        Long requestOwnerId = itemRequestOwner.getId();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("Hummer")
                .requester(itemRequestOwner)
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Puncher")
                .requester(anotherUser)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.saveAll(List.of(itemRequest1, itemRequest2));

        List<ItemRequestDtoOut> allOwnRequests = itemRequestService.getAllOwnItemRequests(requestOwnerId);

        assertThat(allOwnRequests.size()).isEqualTo(1);
        assertThat(allOwnRequests.get(0).getDescription()).isEqualTo("Hummer");
    }

    User createUser(String userPostfix) {
        return User.builder()
                .name(String.format("Name%s", userPostfix))
                .email(String.format("email@email%s.ru", userPostfix))
                .build();
    }
}