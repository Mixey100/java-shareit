package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Item {
    Long id;
    @NotBlank(message = "Должно быть указано наименование вещи")
    String name;
    @NotBlank(message = "Должно быть описание вещи")
    String description;
    @NotNull(message = "Должна быть указана доступность вещи")
    Boolean available;
    Long owner;
}
