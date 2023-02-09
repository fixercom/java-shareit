package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailIsAlreadyInUseException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        checkUniqueEmail(user.getEmail());
        User createdUser = userRepository.save(user);
        log.debug("User saved in the database with id={}: {}", createdUser.getId(), user);
        return createdUser;
    }

    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        log.debug("User with id={} was obtained from the database: {}", id, user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        log.debug("All users were obtained from the database: {}", allUsers);
        return allUsers;
    }

    @Override
    @Transactional
    public User patchUser(Long id, UserDto userDto) {
        User oldUser = getUserById(id);
        checkUniqueEmail(userDto.getEmail());
        User oldUserWithPatch = userMapper.patchUserFromDto(userDto, oldUser);
        User patchedUser = userRepository.save(oldUserWithPatch);
        log.debug("User with id={} successfully updated in the database: {}", id, patchedUser);
        return patchedUser;
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

    private void checkUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailIsAlreadyInUseException(email);
        }
    }
}
