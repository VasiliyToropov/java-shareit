package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ItemRequestServiceImplTest {
    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addItemRequestTest() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужен меч как у Пирамидаголового");

        when(userService.getUser(userId)).thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest addedItemRequest = itemRequestService.addItemRequest(itemRequest, userId);

        assertNotNull(addedItemRequest);
        assertEquals("Нужен меч как у Пирамидаголового", addedItemRequest.getDescription());
        assertEquals(user, addedItemRequest.getRequester());
        assertNotNull(addedItemRequest.getCreated());

        verify(userService, times(1)).getUser(userId);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequestsByUserIdTest() {
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужен меч как у Пирамидаголового");
        itemRequest.setRequester(new User());
        itemRequest.setCreated(LocalDateTime.now());

        when(itemRequestRepository.findAllByRequesterId(userId)).thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequestsByUserId(userId);

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
        assertEquals("Нужен меч как у Пирамидаголового", itemRequests.getFirst().getDescription());

        verify(itemRequestRepository, times(1)).findAllByRequesterId(userId);
    }

    @Test
    void getAllItemRequestsTest() {
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужен меч как у Пирамидаголового");
        itemRequest.setRequester(new User());
        itemRequest.setCreated(LocalDateTime.now());

        when(itemRequestRepository.getAllItemRequests(userId)).thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests(userId);

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
        assertEquals("Нужен меч как у Пирамидаголового", itemRequests.getFirst().getDescription());

        verify(itemRequestRepository, times(1)).getAllItemRequests(userId);
    }

    @Test
    void getItemRequestsByRequestIdTest() {
        Long requestId = 1L;
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Нужен меч как у Пирамидаголового");
        itemRequest.setRequester(new User());
        itemRequest.setCreated(LocalDateTime.now());

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemService.getItemsByRequestId(requestId)).thenReturn(Collections.emptyList());

        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestsByRequestId(requestId, userId);

        assertNotNull(itemRequestDto);
        assertEquals("Нужен меч как у Пирамидаголового", itemRequestDto.getDescription());
        assertNotNull(itemRequestDto.getItems());

        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemService, times(1)).getItemsByRequestId(requestId);
    }

    @Test
    void getItemRequestByIdTest() {
        Long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Нужен меч как у Пирамидаголового");
        itemRequest.setRequester(new User());
        itemRequest.setCreated(LocalDateTime.now());

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequest foundItemRequest = itemRequestService.getItemRequestById(requestId);

        assertNotNull(foundItemRequest);
        assertEquals("Нужен меч как у Пирамидаголового", foundItemRequest.getDescription());

        verify(itemRequestRepository, times(1)).findById(requestId);
    }

}
