package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserTest() {
        UserDto dtoRequest = UserDto.builder().name("user").email("e@e.ru").build();
        when(userRepository.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        UserDto userDtoResponse = userService.createUser(dtoRequest);

        assertThat(userDtoResponse.getName()).isEqualTo("user");
        assertThat(userDtoResponse.getEmail()).isEqualTo("e@e.ru");
        verify(userMapper).toUser(any(UserDto.class));
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDto(any(User.class));
    }

    @Test
    void getUserById_whenUserIsAbsent_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.getUserById(10L));
    }

    @Test
    void getUserById_whenUserIsPresent_thenReturnUserDto() {
        Long userId = 7L;
        User user = User.builder().id(userId).name("user").email("e@e.ru").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(userId);

        assertThat(userDto.getName()).isEqualTo("user");
        assertThat(userDto.getEmail()).isEqualTo("e@e.ru");
        verify(userRepository).findById(anyLong());
        verify(userMapper).toUserDto(any(User.class));
    }

    @Test
    void getUserEntityByIdTest_whenUserIsAbsent_thenThrowException() {
        Long userId = 99L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserEntityById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("There is no user with id=%s in the database", userId);
    }

    @Test
    void getAllUsersTest() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> allUsers = userService.getAllUsers();

        assertThat(allUsers.size()).isEqualTo(2);
        assertThat(allUsers.get(0).getId()).isEqualTo(1);
        assertThat(allUsers.get(1).getId()).isEqualTo(2);
        verify(userRepository).findAll();
        verify(userMapper).toUserDtoList(anyList());
    }

    @Test
    void updateUser_whenSuccessful_thenReturnUserDto() {
        Long userId = 5L;
        User oldUser = User.builder().id(userId).name("Ivan").email("rew@tu.com").build();
        UserDto userDto = UserDto.builder().name("Sergey").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when(userRepository.saveAndFlush(any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        UserDto updatedUser = userService.updateUser(userId, userDto);

        assertThat(updatedUser.getName()).isEqualTo("Sergey");
        assertThat(updatedUser.getEmail()).isEqualTo("rew@tu.com");
        verify(userRepository).findById(anyLong());
        verify(userMapper).updateUserFromDto(any(UserDto.class), any(User.class));
        verify(userRepository).saveAndFlush(any(User.class));
        verify(userMapper).toUserDto(any(User.class));
    }

    @Test
    void deleteUserTest() {
        Long userId = 4L;
        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void checkUserExists_whenUserIsAbsent_thenThrowException() {
        Long userId = 4L;
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> userService.checkUserExists(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("There is no user with id=%s in the database", userId);

    }
}