package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String userIdHeader = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(userIdHeader) Long id) {
        userService.getUserById(id);
        return itemService.getItemsByOwner(id);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam(required = false) String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemService.getItemByText(text);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody Item item, @RequestHeader(userIdHeader) Long userId) {
        userService.getUserById(userId);
        item.setOwner(userId);
        return itemService.createItem(item);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestBody Item item, @RequestHeader(userIdHeader) Long userId,
                              @PathVariable Long id) {
        userService.getUserById(userId);
        item.setOwner(userId);
        item.setId(id);
        return itemService.updateItem(item, id);
    }
}

