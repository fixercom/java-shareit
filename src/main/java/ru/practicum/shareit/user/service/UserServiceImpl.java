package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailIsAlreadyInUseException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
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
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User createdUser = userRepository.save(user);
        log.debug("User saved in the database with id={}: {}", createdUser.getId(), user);
        return userMapper.toUserDto(createdUser);
    }

    public UserDto getUserById(Long id) {
        User user = getUserByIdWithoutCheckAccess(id);
        log.debug("User with id={} was obtained from the database: {}", id, user);
        return userMapper.toUserDto(user);
    }

    @Override
    public User getUserEntityById(Long userId) {
        return getUserByIdWithoutCheckAccess(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        log.debug("All users were obtained from the database: {}", allUsers);
        return userMapper.toUserDtoList(allUsers);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User oldUser = getUserByIdWithoutCheckAccess(id);
        checkUniqueEmail(userDto.getEmail());
        User oldUserWithPatch = userMapper.updateUserFromDto(userDto, oldUser);
        User updatedUser = userRepository.save(oldUserWithPatch);
        log.debug("User with id={} successfully updated in the database: {}", id, updatedUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        checkUserExists(id);
        userRepository.deleteById(id);
    }

    private User getUserByIdWithoutCheckAccess(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public void checkUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    private void checkUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailIsAlreadyInUseException(email);
        }
    }
}
