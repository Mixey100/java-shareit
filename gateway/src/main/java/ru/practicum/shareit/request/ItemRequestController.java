package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDtoRequest dto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.createRequest(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequestorId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getRequestsByRequestorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable("requestId") Long requestId,
                                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getRequestById(requestId, userId);
    }
}