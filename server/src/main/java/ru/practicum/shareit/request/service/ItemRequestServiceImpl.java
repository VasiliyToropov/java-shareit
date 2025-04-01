package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest, Long userId) {

        LocalDateTime now = LocalDateTime.now();

        ItemRequest addedItemRequest = new ItemRequest();

        addedItemRequest.setDescription(itemRequest.getDescription());
        addedItemRequest.setRequester(userService.getUser(userId));
        addedItemRequest.setCreated(now);

        itemRequestRepository.save(addedItemRequest);

        log.info("Добавили новый запрос от пользователя с id: {}", userId);

        return addedItemRequest;
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByUserId(Long userId) {

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId);

        return ItemRequestMapper.INSTANCE.toItemRequestsDto(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {

        List<ItemRequest> allItemRequests = itemRequestRepository.getAllItemRequests(userId);

        return ItemRequestMapper.INSTANCE.toItemRequestsDto(allItemRequests);
    }

    @Override
    public ItemRequestDto getItemRequestsByRequestId(Long requestId, Long userId) {

        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);

        ItemRequest itemRequest = itemRequestOptional.orElseThrow(() -> new NotFoundException("Запрос не найден"));

        ItemRequestDto itemRequestDto = ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequest);

        itemRequestDto.setItems(itemService.getItemsByRequestId(requestId));

        return itemRequestDto;
    }

    @Override
    public ItemRequest getItemRequestById(Long requestId) {
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
        return itemRequestOptional.orElseThrow(() -> new NotFoundException("Запрос не найден"));
    }
}
