package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final ObjectMapper mapper;
    @MockBean
    private final UserService userService;
    private final MockMvc mockMvc;

    @Test
    @SneakyThrows
    void createUser_whenSuccessful_thenReturnIsOk() {
        UserDto userDto = UserDto.builder().name("Name").email("ma@yp.ru").build();
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Name")))
                .andExpect(jsonPath("$.email", is("ma@yp.ru")));
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    @SneakyThrows
    void getUserById_whenSuccessful_thenReturnIsOk() {
        UserDto userDto = UserDto.builder().name("Name2").email("zz@yp.ru").build();
        when(userService.getUserById(anyLong())).thenReturn(userDto);
        mockMvc.perform(get("/users/4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Name2")))
                .andExpect(jsonPath("$.email", is("zz@yp.ru")));
        verify(userService, times(1)).getUserById(4L);
    }

    @Test
    @SneakyThrows
    void getAllUsers() {
        List<UserDto> allUsers = List.of(
                UserDto.builder().build(),
                UserDto.builder().build()
        );
        when(userService.getAllUsers()).thenReturn(allUsers);
        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @SneakyThrows
    void updateUser() {
        UserDto userDto = UserDto.builder().name("Name3").email("qwer@yp.ru").build();
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(userDto);
        mockMvc.perform(patch("/users/77")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Name3")))
                .andExpect(jsonPath("$.email", is("qwer@yp.ru")));
        verify(userService, times(1)).updateUser(77L, userDto);
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        mockMvc.perform(delete("/users/8"))
                .andExpect(status().isOk());
        verify(userService,times(1)).deleteUser(8L);
    }
}