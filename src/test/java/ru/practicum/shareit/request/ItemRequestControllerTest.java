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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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
        ItemRequestDtoOut dtoOut = createItemRequestDtoOut();
        Mockito.when(itemRequestService.createItemRequest(any(ItemRequestDtoIn.class), any(Long.class)))
                .thenReturn(dtoOut);
        ItemRequestDtoIn dtoIn = createDtoInWithDescription("Test description");
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
        ItemRequestDtoIn dtoIn = createDtoInWithDescription(null);
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

    ItemRequestDtoIn createDtoInWithDescription(String description) {
        return ItemRequestDtoIn.builder()
                .description(description)
                .build();
    }

    ItemRequestDtoOut createItemRequestDtoOut() {
        return ItemRequestDtoOut.builder()
                .id(1L)
                .description("Test description")
                .created(LocalDateTime.MIN)
                .build();
    }
}