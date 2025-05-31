package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDtoRequestId;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(ItemRequestDtoRequest itemRequestDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public static List<ItemRequestDto> mapToItemRequestDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemDtoRequestId> itemDtoRequestIds = ItemMapper.mapToItemDtoRequest(items
                .stream()
                .filter(item -> Objects.equals(item.getRequest(), itemRequest))
                .toList());
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(itemDtoRequestIds);
        return itemRequestDto;
    }

    public static List<ItemRequestDto> mapToItemRequestDto(List<ItemRequest> itemRequests, List<Item> items) {
        return itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> requestItems = items.stream()
                            .filter(item -> Objects.equals(item.getRequest(), itemRequest))
                            .toList();
                    return mapToItemRequestDto(itemRequest, requestItems);
                })
                .toList();
    }
}