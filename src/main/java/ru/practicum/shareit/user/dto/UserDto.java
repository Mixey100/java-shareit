package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен содержать символ @")
    String email;
}
