package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface UserService {
    User createUser(UserDto userDto);

    User getUserById(Long id);

    List<User> getAllUsers();

    User patchUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
