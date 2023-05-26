package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemResponseDto> getAllItems(Long userId, PageRequest page);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemDto> searchItems(Long userId, String text, PageRequest page);

    CommentResponseDto createComment(Long userId, CommentDto commentDto, Long itemId);
}
