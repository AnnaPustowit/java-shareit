package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Создан запрос на бронирование вещи с описанием : {}", itemRequestDto.getDescription());
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен список всех запросов пользователя: {}", userId);
        return itemRequestClient.getAllItemRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Получен список всех запросов для пользователя: {}", userId);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @PathVariable Long requestId) {
        log.debug("Получен запрос на бронирование: {}", requestId);
        return itemRequestClient.getItemRequestByRequestId(userId, requestId);
    }
}
