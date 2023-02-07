package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validate.groups.OnCreate;
import ru.practicum.shareit.validate.groups.OnUpdate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    UserDto createUser(@RequestBody @Validated(OnCreate.class) UserDto userDto, HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), userDto);
        return UserMapper.toUserDto(userService.createUser(userDto));
    }

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable Long id, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return UserMapper.toUserDto(userService.getUserById(id));
    }

    @GetMapping
    List<UserDto> getAllUsers(HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return UserMapper.toUserDtoList(userService.getAllUsers());
    }

    @PatchMapping("/{id}")
    UserDto updateUser(@PathVariable Long id,
                       @RequestBody @Validated(OnUpdate.class) UserDto userDto,
                       HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), userDto);
        return UserMapper.toUserDto(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable Long id, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        userService.deleteUser(id);
    }
}
