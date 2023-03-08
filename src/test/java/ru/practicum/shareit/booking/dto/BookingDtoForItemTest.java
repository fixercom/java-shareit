package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.util.Constants;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoForItemTest {
    private final JacksonTester<BookingDtoForItem> jacksonTester;

    @Test
    void serializeInCorrectFormat() throws IOException {
        BookingDtoForItem dto = BookingDtoForItem.builder()
                .id(7L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(10))
                .bookerId(3L)
                .build();

        JsonContent<BookingDtoForItem> jsonContent = jacksonTester.write(dto);
        LocalDateTime startTime = dto.getStart();
        LocalDateTime endTime = dto.getEnd();

        assertThat(jsonContent).extractingJsonPathValue("$.id").isEqualTo(7);
        assertThat(jsonContent).extractingJsonPathValue("$.start")
                .isEqualTo(startTime.format(Constants.FORMATTER));
        assertThat(jsonContent).extractingJsonPathValue("$.end")
                .isEqualTo(endTime.format(Constants.FORMATTER));
        assertThat(jsonContent).extractingJsonPathValue("$.bookerId").isEqualTo(3);
    }
}