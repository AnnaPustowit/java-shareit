package ru.practicum.shareit.item.dao;

import java.util.Collection;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemDao {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> getAllItemsByUsersId(Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    Collection<ItemDto> searchItem(Long userId, String text);
}
