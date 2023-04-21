package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> getAllItemsByUsersId(Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    Collection<ItemDto> searchItem(Long userId, String text);
}
