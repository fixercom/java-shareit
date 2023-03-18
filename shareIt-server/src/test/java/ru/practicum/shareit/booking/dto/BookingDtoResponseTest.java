package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Constants;
import ru.practicum.shareit.util.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoResponseTest {
    private final JacksonTester<BookingDtoResponse> jacksonTester;

    @Test
    void serializeInCorrectFormat() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(7L)
                .name("Gorge")
                .email("fr@gt.com")
                .build();
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(5L)
                .name("Hummer")
                .description("Big")
                .available(true)
                .requestId(null)
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(3L)
                .start(DateUtils.now())
                .end(DateUtils.now().plusDays(10))
                .status(BookingStatus.APPROVED)
                .booker(userDto)
                .item(itemDtoResponse)
                .build();

        JsonContent<BookingDtoResponse> jsonContent = jacksonTester.write(bookingDtoResponse);
        LocalDateTime startTime = bookingDtoResponse.getStart();
        LocalDateTime endTime = bookingDtoResponse.getEnd();

        assertThat(jsonContent).extractingJsonPathValue("$.id").isEqualTo(3);
        assertThat(jsonContent).extractingJsonPathValue("$.start")
                .isEqualTo(startTime.format(Constants.FORMATTER));
        assertThat(jsonContent).extractingJsonPathValue("$.end")
                .isEqualTo(endTime.format(Constants.FORMATTER));
        assertThat(jsonContent).extractingJsonPathValue("$.status").isEqualTo("APPROVED");
        assertThat(jsonContent).extractingJsonPathValue("$.booker.id").isEqualTo(7);
        assertThat(jsonContent).extractingJsonPathValue("$.booker.name").isEqualTo("Gorge");
        assertThat(jsonContent).extractingJsonPathValue("$.booker.email").isEqualTo("fr@gt.com");
        assertThat(jsonContent).extractingJsonPathValue("$.item.id").isEqualTo(5);
        assertThat(jsonContent).extractingJsonPathValue("$.item.name").isEqualTo("Hummer");
        assertThat(jsonContent).extractingJsonPathValue("$.item.description").isEqualTo("Big");
        assertThat(jsonContent).extractingJsonPathValue("$.item.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathValue("$.item.requestId").isEqualTo(null);
    }
}