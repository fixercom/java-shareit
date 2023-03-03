package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final ObjectMapper mapper;
    @MockBean
    private final ItemService itemService;
    private final MockMvc mockMvc;
    private static final String H_SHARER_USER_ID_IS_ABSENT_MESSAGE = "Required request header 'X-Sharer-User-Id'" +
            " for method parameter type Long is not present";

    @Test
    @SneakyThrows
    void createItem_whenSuccessful_thenReturnIsOk() {
        ItemDtoRequest dtoRequest = createDtoRequest("Name", "Description");
        ItemDtoResponse dtoResponse = createDtoResponse(7L, "Name", "Description");
        when(itemService.createItem(any(ItemDtoRequest.class), anyLong())).thenReturn(dtoResponse);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.name", is("Name")))
                .andExpect(jsonPath("$.description", is("Description")));
        verify(itemService, times(1)).createItem(dtoRequest, 3L);
    }

    @Test
    @SneakyThrows
    void createItem_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() {
        ItemDtoRequest dtoRequest = createDtoRequest("Item", "Small");
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemService, never()).createItem(any(ItemDtoRequest.class), anyLong());
    }

    @Test
    @SneakyThrows
    void createComment_whenSuccessful_thenReturnIsOk() {
        CommentDtoRequest dtoRequest = CommentDtoRequest.builder().text("Comment text").build();
        CommentDtoResponse dtoResponse = CommentDtoResponse.builder().text("Comment text").build();
        when(itemService.createComment(anyLong(), any(CommentDtoRequest.class), anyLong())).thenReturn(dtoResponse);
        mockMvc.perform(post("/items/935/comment")
                        .header("X-Sharer-User-Id", 77)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Comment text")));
        verify(itemService, times(1)).createComment(935L, dtoRequest, 77L);
    }

    @Test
    @SneakyThrows
    void createComment_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() {
        CommentDtoRequest dtoRequest = CommentDtoRequest.builder().text("Comment text").build();
        mockMvc.perform(post("/items/935/comment")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemService, never()).createItem(any(ItemDtoRequest.class), anyLong());
    }

    @Test
    @SneakyThrows
    void getItemById_whenSuccessful_thenReturnIsOk() {
        ItemDtoResponseWithDate dtoResponse = ItemDtoResponseWithDate.builder().description("etc").build();
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(dtoResponse);
        mockMvc.perform(get("/items/13")
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("etc")));
        verify(itemService, times(1)).getItemById(13L, 2L);
    }

    @Test
    @SneakyThrows
    void getItemById_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() {
        mockMvc.perform(get("/items/13")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemService, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllItemsByOwnerId_whenSuccessful_thenReturnIsOk() {
        List<ItemDtoResponseWithDate> allItems = List.of(
                ItemDtoResponseWithDate.builder().build(),
                ItemDtoResponseWithDate.builder().build()
        );
        when(itemService.getAllItemsByOwnerId(anyLong())).thenReturn(allItems);
        mockMvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(itemService, times(1)).getAllItemsByOwnerId(7L);
    }

    @Test
    @SneakyThrows
    void getAllItemsByOwnerId_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() {
        mockMvc.perform(get("/items/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemService, never()).getAllItemsByOwnerId(anyLong());
    }

    @Test
    @SneakyThrows
    void updateItem_whenSuccessful_thenReturnIsOk() {
        ItemDtoRequest dtoRequest = createDtoRequest("Hammer", "Wooden");
        ItemDtoResponse dtoResponse = createDtoResponse(9L, "Hammer", "Wooden");
        when(itemService.updateItem(anyLong(), any(ItemDtoRequest.class), anyLong())).thenReturn(dtoResponse);
        mockMvc.perform(patch("/items/8")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(9)))
                .andExpect(jsonPath("$.name", is("Hammer")))
                .andExpect(jsonPath("$.description", is("Wooden")));
        verify(itemService, times(1)).updateItem(8L, dtoRequest, 2L);
    }

    @Test
    @SneakyThrows
    void updateItem_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() {
        ItemDtoRequest dtoRequest = createDtoRequest("Hammer", "Wooden");
        mockMvc.perform(patch("/items/8")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemService, never()).updateItem(anyLong(), any(ItemDtoRequest.class), anyLong());
    }

    @Test
    @SneakyThrows
    void getAvailableItemsByText_whenSuccessful_thenReturnIsOk() {
        List<ItemDtoResponse> availableItems = List.of(
                ItemDtoResponse.builder().build(),
                ItemDtoResponse.builder().build()
        );
        when(itemService.getAvailableItemsByText(anyString())).thenReturn(availableItems);
        mockMvc.perform(get("/items/search?text=Hummer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(itemService, times(1)).getAvailableItemsByText("Hummer");
    }

    ItemDtoRequest createDtoRequest(String name, String description) {
        return ItemDtoRequest.builder()
                .name(name)
                .description(description)
                .available(true)
                .build();
    }

    ItemDtoResponse createDtoResponse(Long id, String name, String description) {
        return ItemDtoResponse.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(true)
                .build();
    }
}