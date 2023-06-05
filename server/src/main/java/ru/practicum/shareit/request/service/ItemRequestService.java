package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllItemRequestsByOwner(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId, PageRequest page);

    ItemRequestDto getItemRequestByRequestId(Long userId, Long requestId);
}
