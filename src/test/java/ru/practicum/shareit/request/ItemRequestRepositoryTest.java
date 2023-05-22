package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    public void setData() {
        user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Аккумуляторная дрель + аккумулятор");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void verifyFindAllByRequestorId() {
        final Sort sort = Sort.by("created").descending();
        List<ItemRequest> resultItemRequestDtoList = itemRequestRepository.findAllByRequestorId(user.getId(), sort);

        assertThat(resultItemRequestDtoList, notNullValue());
        assertThat(resultItemRequestDtoList.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByRequestorIdNot() {
        Page<ItemRequest> resultItemRequestDtoList = itemRequestRepository.findAllByRequestorIdNot(user.getId(), PageRequest.of(0, 10));

        assertThat(resultItemRequestDtoList, notNullValue());
        assertThat("isEmpty", resultItemRequestDtoList.isEmpty());
    }
}
