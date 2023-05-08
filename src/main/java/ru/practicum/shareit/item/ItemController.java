package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        ItemDto newItemDto = itemService.createItem(userId, itemDto);
        log.debug("Добавлена вещь с id : {}", newItemDto.getId());
        return newItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        ItemDto newItemDto = itemService.updateItem(userId, itemId, itemDto);
        log.debug("Вещь с id : " + itemId + " обновлена.");
        return newItemDto;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        ItemResponseDto itemsResponseDto = itemService.findItemById(itemId, userId);
        log.debug("Получение вещи с id : {}", itemId);
        return itemsResponseDto;
    }

    @GetMapping
    public List<ItemResponseDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemResponseDto> findAllItem = itemService.getAllItems(userId);
        log.debug("Получен список всех вещей пользователя : {}", userId);
        return findAllItem;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text) {
        List<ItemDto> items = itemService.searchItems(text);
        log.debug("Получен список вещей по ключевому слову : {}", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody CommentDto commentDto, @PathVariable Long itemId) {
        CommentResponseDto commentNew = itemService.createComment(userId, commentDto, itemId);
        log.debug("Добавлен новый отзыв для вещи : {}", itemId);
        return commentNew;
    }
}
