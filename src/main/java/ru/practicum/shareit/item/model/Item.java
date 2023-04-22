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
    long id;
    String name;
    String description;
    Boolean available;
    long owner;
    String request;
}
