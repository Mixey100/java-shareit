package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    List<Item> getItems();

    Optional<Item> getItemById(Long id);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Long id);
}
