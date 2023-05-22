package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setData() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Аккумуляторная дрель + аккумулятор");
        Item item = new Item();
        item.setOwner(user);
        item.setIsAvailable(Boolean.TRUE);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        userRepository.save(user);
        itemRequest = toItemRequest(itemRequestDto, user);
        ItemRequest itemRequestResult = itemRequestRepository.save(itemRequest);
        item.setRequest(itemRequestResult);
        itemRepository.save(item);
    }

    @Test
    void createItemRequest() {
        ItemRequestDto resultItemRequestDto = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        assertThat(resultItemRequestDto, notNullValue());
        assertThat(resultItemRequestDto.getDescription(), equalTo("Аккумуляторная дрель + аккумулятор"));
    }

    @Test
    void getAllItemRequestsByOwner() {
        List<ItemRequestDto> resultItemRequestDtoList = itemRequestService.getAllItemRequestsByOwner(user.getId());

        assertThat(resultItemRequestDtoList, notNullValue());
        assertThat(resultItemRequestDtoList.size(), equalTo(1));
    }

    @Test
    void getAllItemRequests() {
        User userNew = new User();
        userNew.setName("newName");
        userNew.setEmail("newName@mail.ru");
        User userResult = userRepository.save(userNew);
        List<ItemRequestDto> resultItemRequestDtoList = itemRequestService.getAllItemRequests(userResult.getId(), PageRequest.of(0, 10));

        assertThat(resultItemRequestDtoList, notNullValue());
        assertThat(resultItemRequestDtoList.size(), equalTo(1));
    }

    @Test
    void getItemRequestByRequestId() {
        ItemRequestDto resultItemRequestDto = itemRequestService.getItemRequestByRequestId(user.getId(), itemRequest.getId());

        assertThat(resultItemRequestDto, notNullValue());
        assertThat(resultItemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(resultItemRequestDto.getItems().size(), equalTo(1));
    }
}
