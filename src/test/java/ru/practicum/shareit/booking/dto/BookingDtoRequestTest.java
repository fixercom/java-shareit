package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoRequestTest {
    private final JacksonTester<BookingDtoRequest> jacksonTester;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    @SneakyThrows
    void serializeInCorrectFormat() {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .build();

        JsonContent<BookingDtoRequest> jsonContent = jacksonTester.write(dto);
        LocalDateTime startTime = dto.getStart();
        LocalDateTime endTime = dto.getEnd();

        assertThat(jsonContent).extractingJsonPathValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathValue("$.start").isEqualTo(startTime.format(FORMATTER));
        assertThat(jsonContent).extractingJsonPathValue("$.end").isEqualTo(endTime.format(FORMATTER));
    }
}