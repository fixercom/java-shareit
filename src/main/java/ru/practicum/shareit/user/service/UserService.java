package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(UserDto userDto);

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
