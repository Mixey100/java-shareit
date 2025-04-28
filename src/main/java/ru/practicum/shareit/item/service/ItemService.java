package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItems();

    List<ItemDto> getItemsByOwner(Long userId);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemByText(String text);

    ItemDto createItem(Item item);

    ItemDto updateItem(Item item, Long id);

    void deleteItem(Long id);
}
