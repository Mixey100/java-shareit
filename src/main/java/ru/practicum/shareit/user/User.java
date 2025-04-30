package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
    Long id;
    @NotBlank(message = "Должно быть указано имя пользователя")
    String name;
    @NotBlank(message = "Должен быть указан имейл")
    @Email(message = "В имейле должен содержаться символ @")
    String email;
}
