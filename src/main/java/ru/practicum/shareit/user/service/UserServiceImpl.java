package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailIsAlreadyInUseException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        checkUniqueEmail(user);
        User userFromDatabase = userRepository.save(user);
        log.debug("User saved in the database with id={}: {}", userFromDatabase.getId(), user);
        return userFromDatabase;
    }

    @Override
    public User getUserById(Long id) {
        User userFromDatabase = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        log.debug("User with id={} was obtained from the database: {}", id, userFromDatabase);
        return userFromDatabase;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsersFromDatabase = userRepository.findAll();
        log.debug("All users were obtained from the database: {}", allUsersFromDatabase);
        return allUsersFromDatabase;
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        checkUniqueEmail(newUser);
        User oldUser = getUserById(id);
        User userWithUpdatedFields = patchFieldsForOldUserObject(oldUser, newUser);
        User updatedUserFromDatabase = userRepository.save(userWithUpdatedFields);
        log.debug("User with id={} successfully updated in the database: {}", id, updatedUserFromDatabase);
        return updatedUserFromDatabase;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.debug("User with id={} successfully deleted from the database", id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    private void checkUniqueEmail(User user) {
        String email = user.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new EmailIsAlreadyInUseException(email);
        }
    }

    private User patchFieldsForOldUserObject(User oldUser, User newUser) {
        oldUser.setName(newUser.getName() == null ? oldUser.getName() : newUser.getName());
        oldUser.setEmail(newUser.getEmail() == null ? oldUser.getEmail() : newUser.getEmail());
        return oldUser;
    }
}
