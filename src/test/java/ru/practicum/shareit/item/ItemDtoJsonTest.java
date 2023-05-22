package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testJsonItemDto() throws IOException {
        String jsonString = "{\"name\":\"Аккумуляторная дрель\", \"description\":\"Аккумуляторная дрель + аккумулятор\", \"available\":\"true\"}";

        ItemDto itemDto = this.json.parse(jsonString).getObject();

        assertThat(itemDto.getName()).isEqualTo("Аккумуляторная дрель");
        assertThat(itemDto.getDescription()).isEqualTo("Аккумуляторная дрель + аккумулятор");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
    }
}
