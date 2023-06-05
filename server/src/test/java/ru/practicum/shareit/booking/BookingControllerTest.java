package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;
    private final Long userId = 1L;
    private final int from = 0;
    private final int size = 10;

    private final BookingInputDto bookingInputDto = new BookingInputDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
    );

    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            new BookingDto.Item(1L, "Аккумуляторная дрель"),
            new BookingDto.Booker(1L, "Name"),
            BookingStatus.WAITING
    );

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
        verify(bookingService, times(1)).createBooking(any(), anyLong());
    }

    @Test
    void updateBooking() throws Exception {
        boolean approved = Boolean.TRUE;
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.updateBooking(bookingDto.getId(), userId, approved))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/" + bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
        verify(bookingService, times(1)).updateBooking(bookingDto.getId(), userId, approved);
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(userId, bookingDto.getId()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/" + bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()));
        verify(bookingService, times(1)).getBookingById(userId, bookingDto.getId());
    }

    @Test
    void getAllBookingInfo() throws Exception {
        String state = "FUTURE";
        final Sort sort = Sort.by("start").descending();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        when(bookingService.getAllBookingInfo(userId, state, page))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).getAllBookingInfo(userId, state, page);
    }

    @Test
    void getAllOwnerBookingInfo() throws Exception {
        String state = "ALL";
        final Sort sort = Sort.by("start").descending();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        when(bookingService.getAllOwnerBookingInfo(userId, state, page))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).getAllOwnerBookingInfo(userId, state, page);
    }
}
