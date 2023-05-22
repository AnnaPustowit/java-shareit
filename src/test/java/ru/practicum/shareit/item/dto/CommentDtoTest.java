package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testJsonCommentDto() throws IOException {
        String jsonString = "{\"text\":\"Новый комментарий\"}";

        CommentDto commentDto = this.json.parse(jsonString).getObject();

        assertThat(commentDto.getText()).isEqualTo("Новый комментарий");
    }
}
