package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.*;
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
    void createItemRequest_whenSuccessful_thenReturnIsOk() throws Exception {
        ItemRequestDtoIn dtoIn = ItemRequestDtoIn.builder().description("Test description").build();
        ItemRequestDtoOut dtoOut = createDtoOut(1L, "Test description");
        when(itemRequestService.createItemRequest(any(ItemRequestDtoIn.class), anyLong())).thenReturn(dtoOut);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 5)
                        .content(mapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test description")));
        verify(itemRequestService, times(1)).createItemRequest(dtoIn, 5L);
    }

    @Test
    void getAllOwnItemRequests_whenSuccessful_thenReturnIsOk() throws Exception {
        List<ItemRequestDtoOut> allOwnerItemRequests = List.of(
                createDtoOut(1L, "Request 1"),
                createDtoOut(2L, "Request 2")
        );
        when(itemRequestService.getAllOwnItemRequests(anyLong())).thenReturn(allOwnerItemRequests);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 37)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].description", is("Request 1")))
                .andExpect(jsonPath("$[1].description", is("Request 2")));
        verify(itemRequestService, times(1)).getAllOwnItemRequests(37L);
    }

    @Test
    void getAllNotOwnItemRequests_whenDefaultFromAndSizeParams_thenReturnIsOk() throws Exception {
        List<ItemRequestDtoOut> allNotOwnItemRequests = List.of(
                createDtoOut(3L, "Request 3"),
                createDtoOut(4L, "Request 4")
        );
        when(itemRequestService.getAllNotOwnItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(allNotOwnItemRequests);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].description", is("Request 3")))
                .andExpect(jsonPath("$[1].description", is("Request 4")));
        verify(itemRequestService, times(1)).getAllNotOwnItemRequests(7L, 0, 100);
    }

    @Test
    void getAllNotOwnItemRequests_whenFromAndSizeParamsIsPresent_thenReturnIsOk() throws Exception {
        List<ItemRequestDtoOut> allNotOwnItemRequests = List.of(
                createDtoOut(5L, "Request 3"),
                createDtoOut(6L, "Request 4")
        );
        when(itemRequestService.getAllNotOwnItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(allNotOwnItemRequests);
        mockMvc.perform(get("/requests/all?from=5&size=20")
                        .header("X-Sharer-User-Id", 14)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].description", is("Request 3")))
                .andExpect(jsonPath("$[1].description", is("Request 4")));
        verify(itemRequestService, times(1)).getAllNotOwnItemRequests(14L, 5, 20);
    }

    @Test
    void getItemRequestById_whenSuccessful_thenReturnIsOk() throws Exception {
        ItemRequestDtoOut dtoOut = createDtoOut(4L, "Description");
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(dtoOut);
        mockMvc.perform(get("/requests/4")
                        .header("X-Sharer-User-Id", 9)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.description", is("Description")));
        verify(itemRequestService, times(1)).getItemRequestById(4L, 9L);
    }

    ItemRequestDtoOut createDtoOut(Long id, String description) {
        return ItemRequestDtoOut.builder()
                .id(id)
                .description(description)
                .created(LocalDateTime.MIN)
                .build();
    }
}