package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailIsAlreadyInUseException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.storage.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        checkUniqueUserEmail(user);
        User userFromDatabase = userStorage.save(user);
        log.debug("User saved in the database with id={}: {}", userFromDatabase.getId(), user);
        return userFromDatabase;
    }

    private void checkUniqueUserEmail(User user) {
        String userEmail = user.getEmail();
        boolean isNotUniqueEmail = userStorage.findAll().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(userEmail));
        if (isNotUniqueEmail) {
            throw new EmailIsAlreadyInUseException(userEmail);
        }
    }

    @Override
    public User getUserById(Long id) {
        User userFromDatabase = userStorage.findById(id);
        if (userFromDatabase == null) {
            throw new UserNotFoundException(id);
        }
        log.debug("User with id={} was obtained from the database: {}", id, userFromDatabase);
        return userFromDatabase;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsersFromDatabase = userStorage.findAll();
        log.debug("All users were obtained from the database: {}", allUsersFromDatabase);
        return allUsersFromDatabase;
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        checkUniqueUserEmail(newUser);
        User oldUser = userStorage.findById(id);
        User userWithUpdatedFields = patchFieldsForOldUserObject(oldUser, newUser);
        User updatedUserFromDatabase = userStorage.update(id, userWithUpdatedFields);
        log.debug("User with id={} successfully updated in the database: {}", id, updatedUserFromDatabase);
        return updatedUserFromDatabase;
    }

    private User patchFieldsForOldUserObject(User oldUser, User newUser) {
        oldUser.setName(newUser.getName() == null ? oldUser.getName() : newUser.getName());
        oldUser.setEmail(newUser.getEmail() == null ? oldUser.getEmail() : newUser.getEmail());
        return oldUser;
    }

    @Override
    public void deleteUser(Long id) {
        if (userStorage.delete(id) == 0) {
            throw new UserNotFoundException(id);
        }
        log.debug("User with id={} successfully deleted from the database", id);
    }
}
