package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
            /*@Valid*/ @RequestBody ItemDto itemDto) {
        ItemDto newItemDto = itemService.createItem(userId, itemDto);
        log.debug("Добавлена вещь с id : {}", newItemDto.getId());
        return newItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
            /*@Valid*/ @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        ItemDto newItemDto = itemService.updateItem(userId, itemId, itemDto);
        log.debug("Вещь с id : " + itemId + " обновлена.");
        return newItemDto;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId) {
        ItemResponseDto itemsResponseDto = itemService.getItemById(itemId, userId);
        log.debug("Получение вещи с id : {}", itemId);
        return itemsResponseDto;
    }

    @GetMapping
    public List<ItemResponseDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
            /*@PositiveOrZero*/ @RequestParam(value = "from", defaultValue = "0") Integer from,
            /*@Positive*/ @RequestParam(value = "size", defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemResponseDto> allItems = itemService.getAllItems(userId, page);
        log.debug("Получен список всех вещей пользователя : {}", userId);
        return allItems;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(value = "text") String text,
            /*@Valid @PositiveOrZero*/ @RequestParam(value = "from", defaultValue = "0") Integer from,
            /*@Positive*/ @RequestParam(value = "size", defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        final List<ItemDto> items = itemService.searchItems(userId, text, page);
        log.debug("Получен список вещей по ключевому слову : {}", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
            /*@Valid*/ @RequestBody CommentDto commentDto,
                                            @PathVariable Long itemId) {
        CommentResponseDto commentNew = itemService.createComment(userId, commentDto, itemId);
        log.debug("Добавлен новый отзыв для вещи : {}", itemId);
        return commentNew;
    }
}
