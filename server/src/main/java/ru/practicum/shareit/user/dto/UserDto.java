package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@ToString
public class UserDto {

    private Long id;
    //@NotBlank
    private String name;
    //@NotBlank
    //@Email
    //@EqualsAndHashCode.Include
    private String email;
}
