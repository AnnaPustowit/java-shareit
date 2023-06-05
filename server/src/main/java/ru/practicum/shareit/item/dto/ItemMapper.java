package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getAvailable());
        item.setOwner(itemDto.getOwner());
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        Long request = (item.getRequest() != null) ? item.getRequest().getId() : null;
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(), item.getOwner(), request);
    }

    public static ItemResponseDto toItemResponseDto(Item item) {
        Long request = (item.getRequest() != null) ? item.getRequest().getId() : null;
        return new ItemResponseDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(), item.getOwner(), request);
    }
}
