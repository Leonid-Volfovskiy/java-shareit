package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidationMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotBlank(groups = {ValidationMarker.Create.class})
    private String name;

    @NotBlank(groups = {ValidationMarker.Create.class})
    @Email(groups = {ValidationMarker.Create.class})
    private String email;
}
