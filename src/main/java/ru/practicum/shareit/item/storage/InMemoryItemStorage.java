package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage implements ItemStorage {

    Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteItem(Long id) {
        items.remove(id);
    }

    private long getId() {
        long currentMaxId = items.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(1);
        return ++currentMaxId;
    }
}
