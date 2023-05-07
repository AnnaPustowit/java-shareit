package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemResponseDto> getAllItems(Long userId);

    ItemResponseDto findItemById(Long userId, Long itemId);

    List<ItemDto> searchItems(String text);

    CommentResponseDto createComment(Long userId, CommentDto commentDto, Long itemId);
}
