package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDtoRequest request, Long bookerId);

    List<ItemRequestDto> getRequestsByRequestorId(Long requestorId);

    List<ItemRequestDto> getAllRequest(Long userId);

    ItemRequestDto getRequestById(Long requestId, Long userId);
}
