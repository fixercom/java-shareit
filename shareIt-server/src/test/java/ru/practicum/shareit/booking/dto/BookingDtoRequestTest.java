package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.util.Constants;
import ru.practicum.shareit.util.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoRequestTest {
    private final JacksonTester<BookingDtoRequest> jacksonTester;

    @Test
    void serializeInCorrectFormat() throws IOException {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .itemId(1L)
                .start(DateUtils.now())
                .end(DateUtils.now().plusDays(2))
                .build();

        JsonContent<BookingDtoRequest> jsonContent = jacksonTester.write(dto);
        LocalDateTime startTime = dto.getStart();
        LocalDateTime endTime = dto.getEnd();

        assertThat(jsonContent).extractingJsonPathValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathValue("$.start")
                .isEqualTo(startTime.format(Constants.FORMATTER));
        assertThat(jsonContent).extractingJsonPathValue("$.end")
                .isEqualTo(endTime.format(Constants.FORMATTER));
    }
}