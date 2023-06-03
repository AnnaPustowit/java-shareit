package ru.practicum.shareit.item.dto;

/**
 * TODO Sprint add-controllers.
 */

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemDto {
    private Long id;
    //@NotNull
    //@NotBlank
    private String name;
    //@NotNull
    //@NotBlank
    private String description;
    //@NotNull
    private Boolean available;
    private User owner;
    private Long requestId;
}
