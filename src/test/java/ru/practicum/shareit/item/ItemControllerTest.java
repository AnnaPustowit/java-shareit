package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;
    private final Long userId = 1L;
    private final int from = 0;
    private final int size = 10;

    private final CommentDto commentDto =
            new CommentDto("Новый комментарий");
    private final CommentResponseDto commentResponseDto =
            new CommentResponseDto(1L, "Новый комментарий", "Name", null);
    private final ItemDto itemDto =
            new ItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", Boolean.TRUE, null, null);
    private final ItemResponseDto itemResponseDto =
            new ItemResponseDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", Boolean.TRUE, null, null, null, null, null);

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
        verify(itemService, times(1)).createItem(anyLong(), any());
    }

    @Test
    void updateItem() throws Exception {
        itemDto.setName("Пила");
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/" + userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
        verify(itemService, times(1)).updateItem(anyLong(), anyLong(), any());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()));
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @Test
    void getAllItems() throws Exception {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        when(itemService.getAllItems(userId, page))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1)).getAllItems(userId, page);
    }

    @Test
    void searchItems() throws Exception {
        String text = "Дрель";
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        when(itemService.searchItems(userId, text, page))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1)).searchItems(userId, text, page);
    }

    @Test
    void createComment() throws Exception {
        Long itemId = 1L;
        when(itemService.createComment(userId, commentDto, itemId))
                .thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()));
        verify(itemService, times(1)).createComment(userId, commentDto, itemId);
    }
}
