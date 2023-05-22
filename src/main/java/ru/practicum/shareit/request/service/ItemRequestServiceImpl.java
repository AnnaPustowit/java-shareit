package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ValidateEntityException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + userId + " не найден."));
        final ItemRequest itemRequest = toItemRequest(itemRequestDto, user);
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByOwner(Long userId) {
        if (!validateUser(userId))
            throw new ValidateEntityException("Пользователь с id : " + userId + " не найден.");
        final Sort sort = Sort.by("created").descending();
        final List<ItemRequestDto> itemRequestDto = itemRequestRepository.findAllByRequestorId(userId, sort)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return addDataToRequest(itemRequestDto);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, PageRequest page) {
        if (!validateUser(userId))
            throw new ValidateEntityException("Пользователь с id : " + userId + " не найден.");
        final List<ItemRequestDto> itemRequestDto = itemRequestRepository.findAllByRequestorIdNot(userId, page)
                .map(ItemRequestMapper::toItemRequestDto)
                .getContent();
        return addDataToRequest(itemRequestDto);
    }

    @Override
    public ItemRequestDto getItemRequestByRequestId(Long userId, Long requestId) {
        if (!validateUser(userId))
            throw new ValidateEntityException("Пользователь с id : " + userId + " не найден.");
        final ItemRequestDto itemRequestDto = toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ValidateEntityException("Запрос на бронирование вещи не найден.")));
        final List<ItemDto> itemDtoList = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
    }

    private boolean validateUser(Long userId) {
        return userRepository.existsById(userId);
    }

    private List<ItemRequestDto> addDataToRequest(List<ItemRequestDto> listItemRequestDto) {
        final List<Long> listRequestIds = listItemRequestDto
                .stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        final List<ItemDto> itemDtoList = itemRepository.findAllByRequestIdIn(listRequestIds)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        listItemRequestDto
                .forEach(r -> r.setItems(itemDtoList
                        .stream()
                        .filter(itemDto -> itemDto.getRequestId().equals(r.getId()))
                        .collect(Collectors.toList())));
        return listItemRequestDto;
    }
}
