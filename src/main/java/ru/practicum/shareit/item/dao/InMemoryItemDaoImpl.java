package ru.practicum.shareit.item.dao;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryItemDaoImpl implements ItemDao {
    private final UserDao userDao;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Long>> listItemsByUsersId = new HashMap<>();
    private long counter = 0L;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userDao.findUserById(userId);
        validateName(itemDto.getName());
        Item item = ItemMapper.toItem(++counter, userId, itemDto);
        items.put(counter, item);
        addItemToList(userId, item);
        log.info("Создан предмет: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userDao.findUserById(userId);
        validateItem(itemId);
        validate(userId, itemId);
        Item item = items.get(itemId);
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        if (name != null) {
            validateName(name);
            item.setName(name);
        }

        if (description != null) {
            item.setDescription(description);
        }

        if (available != null) {
            item.setAvailable(available);
        }

        addItemToList(userId, item);
        items.put(itemId, item);
        log.info("Обновлен предмет: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemsByUsersId(long userId) {
        userDao.findUserById(userId);
        return listItemsByUsersId.get(userId).stream().map(items::get).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        userDao.findUserById(userId);
        validateItem(itemId);
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public Collection<ItemDto> searchItem(long userId, String text) {
        userDao.findUserById(userId);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return items.values().stream().filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void validate(long userId, long itemId) {
        Item item = items.get(itemId);
        if (item.getOwner() != userId) {
            throw new NotFoundException("Неверный id пользователя - " + userId);
        }
    }

    private void validateItem(long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет с id : " + id + " не найден");
        }
    }

    private void validateName(String name) {
        if (name.isEmpty()) {
            throw new NotFoundException("Название не может быть пустым!");
        }
    }

    private void addItemToList(long id, Item item) {
        Set<Long> itemsList = listItemsByUsersId.getOrDefault(id, new HashSet<>());
        itemsList.add(item.getId());
        listItemsByUsersId.put(id, itemsList);
    }
}
