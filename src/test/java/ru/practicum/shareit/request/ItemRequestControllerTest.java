package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private final ObjectMapper mapper;
    @MockBean
    private final ItemRequestService itemRequestService;
    private final MockMvc mockMvc;

    @Test
    void testCreateItemRequest_IsOk() throws Exception {
        ItemRequestDtoOut dtoOut = createDtoOut(1L, "Test description");
        Mockito.when(itemRequestService.createItemRequest(any(ItemRequestDtoIn.class), any(Long.class)))
                .thenReturn(dtoOut);
        ItemRequestDtoIn dtoIn = createDtoIn("Test description");
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test description")));
    }

    @Test
    void testCreateItemRequest_IsBadRequest_WithNullDescription() throws Exception {
        ItemRequestDtoIn dtoIn = createDtoIn(null);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is("Description must not be empty or null")));
    }

    @Test
    void testGetAllOwnItemRequests_isOk() throws Exception {
        List<ItemRequestDtoOut> allOwnerItemRequests = List.of(
                createDtoOut(1L, "Request 1"),
                createDtoOut(2L, "Request 2")
        );
        Mockito.when(itemRequestService.getAllOwnItemRequests(anyLong()))
                .thenReturn(allOwnerItemRequests);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].description", is("Request 1")))
                .andExpect(jsonPath("$[1].description", is("Request 2")));
    }

    @Test
    void testGetAllNotOwnItemRequests_isOk() throws Exception {
        List<ItemRequestDtoOut> allNotOwnItemRequests = List.of(
                createDtoOut(3L, "Request 3"),
                createDtoOut(4L, "Request 4")
        );
        Mockito.when(itemRequestService.getAllNotOwnItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(allNotOwnItemRequests);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].description", is("Request 3")))
                .andExpect(jsonPath("$[1].description", is("Request 4")));
    }

    ItemRequestDtoIn createDtoIn(String description) {
        return ItemRequestDtoIn.builder()
                .description(description)
                .build();
    }

    ItemRequestDtoOut createDtoOut(Long id, String description) {
        return ItemRequestDtoOut.builder()
                .id(id)
                .description(description)
                .created(LocalDateTime.MIN)
                .build();
    }

}