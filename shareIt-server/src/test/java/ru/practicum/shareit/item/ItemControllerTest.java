package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotOwnerItemException;
import ru.practicum.shareit.exception.UserDidNotBookingItemException;
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

    @Test
    void createItem_whenSuccessful_thenReturnIsOk() throws Exception {
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
    void createComment_whenSuccessful_thenReturnIsOk() throws Exception {
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
    void createComment_whenUserDidNotBookingItem_thenReturnIsBadRequest() throws Exception {
        Long userId = 77L;
        Long itemId = 246L;
        CommentDtoRequest dtoRequest = CommentDtoRequest.builder().text("Comment text").build();
        when(itemService.createComment(anyLong(), any(CommentDtoRequest.class), anyLong()))
                .thenThrow(new UserDidNotBookingItemException(userId, itemId));
        mockMvc.perform(post("/items/246/comment")
                        .header("X-Sharer-User-Id", 77)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(String.format("The user with id=%s did not book" +
                        " the item with id=%s", userId, itemId))));
        verify(itemService, times(1)).createComment(itemId, dtoRequest, userId);
    }

    @Test
    void getItemById_whenSuccessful_thenReturnIsOk() throws Exception {
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
    void getAllItemsByOwnerId_whenSuccessful_thenReturnIsOk() throws Exception {
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
    void updateItem_whenSuccessful_thenReturnIsOk() throws Exception {
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
    void updateItem_whenNotOwnerItem_thenReturnIsForbidden() throws Exception {
        Long itemId = 15L;
        Long userId = 66L;
        ItemDtoRequest dtoRequest = createDtoRequest("Hammer", "Wooden");
        when(itemService.updateItem(anyLong(), any(ItemDtoRequest.class), anyLong()))
                .thenThrow(new NotOwnerItemException(itemId, userId));
        mockMvc.perform(patch("/items/15")
                        .header("X-Sharer-User-Id", 66)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode", is(403)))
                .andExpect(jsonPath("$.error", is(String.format("User with id=%s is not the owner of" +
                        " the item with id=%s", userId, itemId))));
        verify(itemService, times(1)).updateItem(itemId, dtoRequest, userId);
    }

    @Test
    void getAvailableItemsByText_whenSuccessful_thenReturnIsOk() throws Exception {
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