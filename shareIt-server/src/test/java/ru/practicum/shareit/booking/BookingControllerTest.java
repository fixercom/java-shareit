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
import ru.practicum.shareit.booking.entity.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectBookingDatesException;
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
    void createBooking_whenDatesAreIncorrect_thenReturnIsBadRequest() throws Exception {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        BookingDtoRequest dtoRequest = BookingDtoRequest.builder().build();
        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong()))
                .thenThrow(new IncorrectBookingDatesException(startDate, endDate));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is(String.format("The end date of the booking %s cannot" +
                        " be earlier or equal the start date %s", endDate, startDate))));
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
    void getAllByBookerId_whenDefaultFromAndSizeParams_thenReturnIsOk() throws Exception {
        List<BookingDtoResponse> allBookings = List.of(
                BookingDtoResponse.builder().build(),
                BookingDtoResponse.builder().build()
        );
        when(bookingService.getAllByBookerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(allBookings);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(bookingService, times(1))
                .getAllByBookerId(7L, BookingState.ALL, 0, 100);
    }

    @Test
    void getAllByBookerId_whenFromAndSizeParamsIsPresent_thenReturnIsOk() throws Exception {
        List<BookingDtoResponse> allBookings = List.of(
                BookingDtoResponse.builder().build(),
                BookingDtoResponse.builder().build()
        );
        when(bookingService.getAllByBookerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(allBookings);
        mockMvc.perform(get("/bookings?from=10&size=5")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(bookingService, times(1))
                .getAllByBookerId(7L, BookingState.ALL, 10, 5);
    }

    @Test
    void getAllByBookerId_whenUnknownState_thenReturnIsBadRequest() throws Exception {
        when(bookingService.getAllByBookerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenThrow(new UnknownStateException(BookingState.UNSUPPORTED_STATUS));
        mockMvc.perform(get("/bookings?state=UNSUPPORTED_STATUS&from=10&size=5")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));
        verify(bookingService, times(1))
                .getAllByBookerId(7L, BookingState.UNSUPPORTED_STATUS, 10, 5);
    }

    @Test
    void getAllByItemOwnerId_whenDefaultFromAndSizeParams_thenReturnIsOk() throws Exception {
        List<BookingDtoResponse> allBookings = List.of(
                BookingDtoResponse.builder().build(),
                BookingDtoResponse.builder().build()
        );
        when(bookingService.getAllByItemOwnerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(allBookings);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(bookingService, times(1))
                .getAllByItemOwnerId(7L, BookingState.ALL, 0, 100);
    }

    @Test
    void getAllByItemOwnerId_whenFromAndSizeParamsIsPresent_thenReturnIsOk() throws Exception {
        List<BookingDtoResponse> allBookings = List.of(
                BookingDtoResponse.builder().build(),
                BookingDtoResponse.builder().build()
        );
        when(bookingService.getAllByItemOwnerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(allBookings);
        mockMvc.perform(get("/bookings/owner?from=10&size=5")
                        .header("X-Sharer-User-Id", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
        verify(bookingService, times(1))
                .getAllByItemOwnerId(7L, BookingState.ALL, 10, 5);
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