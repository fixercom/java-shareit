package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.state.BookingState;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper mapper;
    @MockBean
    private final BookingClient bookingClient;
    private final MockMvc mockMvc;
    private static final String H_SHARER_USER_ID_IS_ABSENT_MESSAGE = "Required request header 'X-Sharer-User-Id'" +
            " for method parameter type Long is not present";

    @Test
    void createBooking_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        BookingDtoRequest dtoRequest = BookingDtoRequest.builder().build();
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(bookingClient, never()).createBooking(any(BookingDtoRequest.class), anyLong());
    }

    @Test
    void getBookingById_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(bookingClient, never()).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getAllByBookerId_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(bookingClient, never()).getAllByBookerId(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getAllByItemOwnerId_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(bookingClient, never()).getAllByItemOwnerId(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getAllByItemOwnerId_whenIncorrectSizeParam_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner?size=-10")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is("getAllByItemOwnerId.size: must be" +
                        " greater than or equal to 1")));
        verify(bookingClient, never()).getAllByItemOwnerId(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void updateBooking_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        BookingDtoRequest dtoRequest = BookingDtoRequest.builder().build();
        mockMvc.perform(patch("/bookings/14?approved=true")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(bookingClient, never()).updateBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void updateBooking_whenApproveParamIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/14")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is("Required request parameter 'approved' for" +
                        " method parameter type Boolean is not present")));
        verify(bookingClient, never()).updateBooking(anyLong(), anyLong(), anyBoolean());
    }
}