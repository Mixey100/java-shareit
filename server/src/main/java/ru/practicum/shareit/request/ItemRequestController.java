package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDtoRequest itemRequestDtoRequest,
                                        @RequestHeader(value = USER_ID_HEADER) Long requestorId) {
        return itemRequestService.createRequest(itemRequestDtoRequest, requestorId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByRequestorId(@RequestHeader(value = USER_ID_HEADER) Long requestorId) {
        return itemRequestService.getRequestsByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(value = USER_ID_HEADER) Long requestorId) {
        return itemRequestService.getAllRequest(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId, @RequestHeader(value = USER_ID_HEADER) Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
