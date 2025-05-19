package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    CommentDto addComment(CommentDtoRequest commentDtoRequest, Long itemId, Long userId);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long id, Long userId);

    void deleteItem(Long id);

    List<ItemDto> getItemByText(String text);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByOwnerId(Long ownerId);

}
