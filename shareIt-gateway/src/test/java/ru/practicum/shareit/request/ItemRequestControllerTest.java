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

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private final ObjectMapper mapper;
    @MockBean
    private final ItemRequestClient itemRequestClient;
    private final MockMvc mockMvc;

    private static final String H_SHARER_USER_ID_IS_ABSENT_MESSAGE = "Required request header 'X-Sharer-User-Id'" +
            " for method parameter type Long is not present";

    @Test
    void createItemRequest_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        ItemRequestDtoIn dtoIn = ItemRequestDtoIn.builder()
                .description("Description")
                .build();
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemRequestClient, never()).createItemRequest(any(ItemRequestDtoIn.class), anyLong());
    }

    @Test
    void getAllOwnItemRequests_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemRequestClient, never()).getAllOwnItemRequests(anyLong());
    }

    @Test
    void getAllNotOwnItemRequests_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemRequestClient, never()).getAllOwnItemRequests(anyLong());
    }

    @Test
    void getItemRequestById_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/requests/5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(itemRequestClient, never()).getAllOwnItemRequests(anyLong());
    }
}