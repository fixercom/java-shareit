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
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.entity.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingEndDateBeforeStartDateException;
import ru.practicum.shareit.exception.ItemNotAvailableForBookingException;
import ru.practicum.shareit.exception.NotPossibleChangeBookingStatusException;
import ru.practicum.shareit.exception.UnknownStateException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper mapper;
    @MockBean
    private final BookingService bookingService;
    private final MockMvc mockMvc;

    private static final String H_SHARER_USER_ID_IS_ABSENT_MESSAGE = "Required request header 'X-Sharer-User-Id'" +
            " for method parameter type Long is not present";

    @Test
    void createBooking_whenSuccessful_thenReturnIsOk() throws Exception {
        BookingDtoRequest dtoRequest = BookingDtoRequest.builder().build();
        BookingDtoResponse dtoResponse = BookingDtoResponse.builder().status(BookingStatus.WAITING).build();
        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong())).thenReturn(dtoResponse);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("WAITING")));
        verify(bookingService, times(1)).createBooking(dtoRequest, 3L);
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenReturnIsBadRequest() throws Exception {
        Long itemId = 1L;
        BookingDtoRequest dtoRequest = BookingDtoRequest.builder().build();
        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong()))
                .thenThrow(new ItemNotAvailableForBookingException(itemId));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error",
                        is(String.format("Item with id=%s is not available for booking", itemId))));
        verify(bookingService, times(1)).createBooking(dtoRequest, 3L);
    }

    @Test
    void createBooking_whenDatesAreIncorrect_thenReturnIsBadRequest() throws Exception {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        BookingDtoRequest dtoRequest = BookingDtoRequest.builder().build();
        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong()))
                .thenThrow(new BookingEndDateBeforeStartDateException(startDate, endDate));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(String.format("The end date of the booking %s cannot" +
                        " be earlier than the start date %s", endDate, startDate))));
        verify(bookingService, times(1)).createBooking(dtoRequest, 3L);
    }

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
        verify(bookingService, never()).createBooking(any(BookingDtoRequest.class), anyLong());
    }

    @Test
    void getBookingById_whenSuccessful_thenReturnIsOk() throws Exception {
        BookingDtoResponse dtoResponse = BookingDtoResponse.builder().id(5L).status(BookingStatus.WAITING).build();
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(dtoResponse);
        mockMvc.perform(get("/bookings/5")
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.status", is("WAITING")));
        verify(bookingService, times(1)).getBookingById(5L, 2L);
    }

    @Test
    void getBookingById_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(bookingService, never()).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getAllByBookerId_whenDefaultFromAndSizeParams_thenReturnIsOk() throws Exception {
        List<BookingDtoResponse> allBookings = List.of(
                BookingDtoResponse.builder().build(),
                BookingDtoResponse.builder().build()
        );
        when(bookingService.getAllByBookerId(anyLong(), any(State.class), anyInt(), anyInt())).thenReturn(allBookings);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(bookingService, times(1)).getAllByBookerId(7L, State.ALL, 0, 100);
    }

    @Test
    void getAllByBookerId_whenFromAndSizeParamsIsPresent_thenReturnIsOk() throws Exception {
        List<BookingDtoResponse> allBookings = List.of(
                BookingDtoResponse.builder().build(),
                BookingDtoResponse.builder().build()
        );
        when(bookingService.getAllByBookerId(anyLong(), any(State.class), anyInt(), anyInt())).thenReturn(allBookings);
        mockMvc.perform(get("/bookings?from=10&size=5")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(bookingService, times(1)).getAllByBookerId(7L, State.ALL, 10, 5);
    }

    @Test
    void getAllByBookerId_whenUnknownState_thenReturnIsBadRequest() throws Exception {
        when(bookingService.getAllByBookerId(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenThrow(new UnknownStateException(State.UNSUPPORTED_STATUS));
        mockMvc.perform(get("/bookings?state=UNSUPPORTED_STATUS&from=10&size=5")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));
        verify(bookingService, times(1))
                .getAllByBookerId(7L, State.UNSUPPORTED_STATUS, 10, 5);
    }

    @Test
    void getAllByBookerId_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(bookingService, never()).getAllByBookerId(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void getAllByItemOwnerId_whenDefaultFromAndSizeParams_thenReturnIsOk() throws Exception {
        List<BookingDtoResponse> allBookings = List.of(
                BookingDtoResponse.builder().build(),
                BookingDtoResponse.builder().build()
        );
        when(bookingService.getAllByItemOwnerId(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(allBookings);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(bookingService, times(1))
                .getAllByItemOwnerId(7L, State.ALL, 0, 100);
    }

    @Test
    void getAllByItemOwnerId_whenFromAndSizeParamsIsPresent_thenReturnIsOk() throws Exception {
        List<BookingDtoResponse> allBookings = List.of(
                BookingDtoResponse.builder().build(),
                BookingDtoResponse.builder().build()
        );
        when(bookingService.getAllByItemOwnerId(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(allBookings);
        mockMvc.perform(get("/bookings/owner?from=10&size=5")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(bookingService, times(1))
                .getAllByItemOwnerId(7L, State.ALL, 10, 5);
    }

    @Test
    void getAllByItemOwnerId_whenXSharerUserIdIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(H_SHARER_USER_ID_IS_ABSENT_MESSAGE)));
        verify(bookingService, never()).getAllByItemOwnerId(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void getAllByItemOwnerId_whenIncorrectSizeParam_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner?size=-10")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is("getAllByItemOwnerId.size: must be" +
                        " greater than or equal to 1")));
        verify(bookingService, never()).getAllByItemOwnerId(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void updateBooking_whenSuccessful_thenReturnIsOk() throws Exception {
        BookingDtoRequest dtoRequest = BookingDtoRequest.builder().build();
        BookingDtoResponse dtoResponse = BookingDtoResponse.builder().status(BookingStatus.APPROVED).build();
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(dtoResponse);
        mockMvc.perform(patch("/bookings/14?approved=true")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
        verify(bookingService, times(1)).updateBooking(2L, 14L, true);
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
        verify(bookingService, never()).updateBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void updateBooking_whenApproveParamIsAbsent_thenReturnIsBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/14")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is("Required request parameter 'approved' for" +
                        " method parameter type Boolean is not present")));
        verify(bookingService, never()).updateBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void updateBooking_whenNotPossibleChangeBookingStatus_thenReturnIsBadRequest() throws Exception {
        Long bookingId = 33L;
        BookingDtoRequest dtoRequest = BookingDtoRequest.builder().build();
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotPossibleChangeBookingStatusException(bookingId));
        mockMvc.perform(patch("/bookings/14?approved=true")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(
                        String.format("The status for the booking with id=%s cannot be changed", bookingId))));
        verify(bookingService).updateBooking(anyLong(), anyLong(), anyBoolean());
    }
}