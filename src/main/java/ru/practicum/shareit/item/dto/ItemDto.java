package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidationMarker;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(groups = {ValidationMarker.Create.class})
    private String name;

    @NotBlank(groups = {ValidationMarker.Create.class})
    private String description;

    @NotNull(groups = {ValidationMarker.Create.class})
    private Boolean available;

    private User owner;

    private Long requestId;
}
