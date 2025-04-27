package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDto> getItems();

    List<ItemDto> getItemsByOwner(Long userId);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemByText(String text);

    ItemDto createItem(Item item);

    ItemDto updateItem(Item item);

    void deleteItem(Long id);
}
