package ru.practicum.shareit.item.dao;

import java.util.Collection;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemDao {

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    Collection<ItemDto> getAllItemsByUsersId(long userId);

    ItemDto getItemById(long userId, long itemId);

    Collection<ItemDto> searchItem(long userId, String text);
}
