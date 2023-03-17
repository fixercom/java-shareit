package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class UserServiceImplIntegrationTest {
    private final UserServiceImpl userService;

    @Test
    void createUserTest() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("iv@rest.ru")
                .build();

        UserDto userDtoFromService = userService.createUser(userDto);

        assertThat(userDtoFromService.getId()).isNotNull();
        assertThat(userDtoFromService)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(userDto);
    }
}