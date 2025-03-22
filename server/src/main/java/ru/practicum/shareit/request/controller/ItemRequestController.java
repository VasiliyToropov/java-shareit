package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequest addItemRequest(@RequestBody ItemRequest itemRequest, @RequestHeader(headerName) Long userId) {
        return itemRequestService.addItemRequest(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByUserId(@RequestHeader(headerName) Long userId) {
        return itemRequestService.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(headerName) Long userId) {
        return itemRequestService.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestsByRequestId(@PathVariable Long requestId, @RequestHeader(headerName) Long userId) {
        return itemRequestService.getItemRequestsByRequestId(requestId, userId);
    }
}
