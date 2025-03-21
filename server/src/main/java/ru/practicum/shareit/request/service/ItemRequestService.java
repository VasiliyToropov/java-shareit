package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    public ItemRequest addItemRequest(ItemRequest itemRequest, Long userId);

    public List<ItemRequestDto> getItemRequestsByUserId(Long userId);

    public List<ItemRequestDto> getAllItemRequests(Long userId);

    public ItemRequestDto getItemRequestsByRequestId(Long requestId, Long userId);

    public ItemRequest getItemRequestById(Long requestId);
}
