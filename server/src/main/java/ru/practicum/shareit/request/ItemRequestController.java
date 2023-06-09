package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestDtoService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        final ItemRequestDto itemRequestDtoNew = itemRequestDtoService.createItemRequest(userId, itemRequestDto);
        log.debug("Создан запрос на бронирование вещи с описание: {}", itemRequestDto.getDescription());
        return itemRequestDtoNew;
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        final List<ItemRequestDto> itemRequestDto = itemRequestDtoService.getAllItemRequestsByOwner(userId);
        log.debug("Получен список всех запросов пользователя: {}", userId);
        return itemRequestDto;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        final Sort sort = Sort.by("created").descending();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        final List<ItemRequestDto> itemRequestDto = itemRequestDtoService.getAllItemRequests(userId, page);
        log.debug("Получен список всех запросов для пользователя: {}", userId);
        return itemRequestDto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long requestId) {
        final ItemRequestDto itemRequestDto = itemRequestDtoService.getItemRequestByRequestId(userId, requestId);
        log.debug("Получен запрос на бронирование: {}", requestId);
        return itemRequestDto;
    }
}
