package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getItemsByOwnerId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemByI(@PathVariable Long id) {
        return itemClient.getItemById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestParam("text") String text) {
        return itemClient.getItemByText(text);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDtoRequest dto,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.createItem(dto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDtoRequest dto, @PathVariable Long itemId,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.addComment(dto, itemId, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDtoRequest newItem, @PathVariable("id") Long id,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.updateItem(newItem, id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") Long id) {
        itemClient.deleteItem(id);
    }
}