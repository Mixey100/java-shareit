package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> getItemsByOwnerId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemByText(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> createItem(ItemDtoRequest dto, Long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> addComment(CommentDtoRequest dto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, dto);
    }

    public ResponseEntity<Object> updateItem(ItemDtoRequest dto, Long itemId, Long userId) {
        return patch("/" + itemId, userId, dto);
    }

    public void deleteItem(Long itemId) {
        delete("", itemId);
    }
}