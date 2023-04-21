package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        return itemDao.createItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return itemDao.updateItem(userId, itemId, itemDto);
    }

    @Override
    public Collection<ItemDto> getAllItemsByUsersId(Long userId) {
        return itemDao.getAllItemsByUsersId(userId);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        return itemDao.getItemById(userId, itemId);
    }

    @Override
    public Collection<ItemDto> searchItem(Long userId, String text) {
        return itemDao.searchItem(userId, text);
    }
}
