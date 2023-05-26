package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class BookingInputDtoJsonTest {
    @Autowired
    private JacksonTester<BookingInputDto> json;

    @Test
    void testJsonBookingRequestDto() throws IOException {
        String jsonString = "{\"itemId\":\"1\", \"start\":\"2023-05-22T18:00:00\", \"end\":\"2023-05-23T18:00:00\"}";

        BookingInputDto bookingInputDto = this.json.parse(jsonString).getObject();

        assertThat(bookingInputDto.getItemId()).isEqualTo(1L);
        assertThat(bookingInputDto.getStart()).isEqualTo(LocalDateTime.of(2023, 5, 22, 18, 0, 0));
        assertThat(bookingInputDto.getEnd()).isEqualTo(LocalDateTime.of(2023, 5, 23, 18, 0, 0));
    }
}
