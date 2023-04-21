package ru.practicum.shareit.item.model;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    String request;
}
