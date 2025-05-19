package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public List<ItemDto> getItemsByOwnerId(@RequestHeader(value = userIdHeader, required = false) Long ownerId) {
        userService.getUserById(ownerId);
        return itemService.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam(required = false) String text) {
        return itemService.getItemByText(text);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto item,
                              @RequestHeader(value = userIdHeader, required = false) Long ownerId) {
        userService.getUserById(ownerId);
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestBody ItemDto item,
                              @RequestHeader(value = userIdHeader, required = false) Long ownerId,
                              @PathVariable Long id) {
        userService.getUserById(ownerId);
        return itemService.updateItem(item, id, ownerId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDtoRequest commentDto, @PathVariable Long itemId,
                                 @RequestHeader(value = userIdHeader, required = false) Long userId) {
        return itemService.addComment(commentDto, itemId, userId);
    }
}

