package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final ObjectMapper mapper;
    @MockBean
    private final ItemClient itemClient;
    private final MockMvc mockMvc;
    private static final String H_SHARER_USER_ID_IS_ABSENT_MESSAGE = "Required request header 'X-Sharer-User-Id'" +
            " for method parameter type Long is not present";

    @Test
    void createItem_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        ItemDtoRequest dtoRequest = createDtoRequest("Item", "Small");
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemClient, never()).createItem(any(ItemDtoRequest.class), anyLong());
    }

    @Test
    void createItem_whenItemNameIsNull_thenReturnIsBadRequest() throws Exception {
        ItemDtoRequest dtoRequest = createDtoRequest(null, "Small");
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is("name must not be empty or null")));
        verify(itemClient, never()).createItem(any(ItemDtoRequest.class), anyLong());
    }

    @Test
    void createComment_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        CommentDtoRequest dtoRequest = CommentDtoRequest.builder().text("Comment text").build();
        mockMvc.perform(post("/items/935/comment")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemClient, never()).createItem(any(ItemDtoRequest.class), anyLong());
    }

    @Test
    void getItemById_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/items/13")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemClient, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    void getAllItemsByOwnerId_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/items/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemClient, never()).getAllItemsByOwnerId(anyLong());
    }

    @Test
    void updateItem_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        ItemDtoRequest dtoRequest = createDtoRequest("Hammer", "Wooden");
        mockMvc.perform(patch("/items/8")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemClient, never()).updateItem(anyLong(), any(ItemDtoRequest.class), anyLong());
    }

    ItemDtoRequest createDtoRequest(String name, String description) {
        return ItemDtoRequest.builder()
                .name(name)
                .description(description)
                .available(true)
                .build();
    }
}