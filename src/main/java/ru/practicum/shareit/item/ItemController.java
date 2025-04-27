package ru.practicum.shareit.item;

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

    private final ItemService service;
    private final UserService userService;

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long id) {
        userService.getUserById(id);
        return service.getItemsByOwner(id);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        return service.getItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam(required = false) String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return service.getItemByText(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userService.getUserById(userId);
        item.setOwner(userId);
        return service.createItem(item);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestBody Item item, @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @PathVariable Long id) {
        userService.getUserById(userId);
        item.setOwner(userId);
        item.setId(id);
        return service.updateItem(item);
    }
}

