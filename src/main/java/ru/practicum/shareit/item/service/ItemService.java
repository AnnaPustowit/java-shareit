package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    Collection<ItemDto> getAllItemsByUsersId(long userId);

    ItemDto getItemById(long userId, long itemId);

    Collection<ItemDto> searchItem(long userId, String text);
}
