package ru.practicum.shareit.item;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private Comment comment;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User Name");

        item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setOwner(user);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment Text");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthorName(user.getName());
        comment.setItem(item);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
    }

    // Тесты для метода addItem
    @Test
    void addItem_ShouldSaveItem() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item savedItem = itemService.addItem(item, user.getId());

        assertNotNull(savedItem);
        assertEquals(item.getName(), savedItem.getName());
        assertEquals(item.getDescription(), savedItem.getDescription());
        assertEquals(item.getAvailable(), savedItem.getAvailable());
        assertEquals(user, savedItem.getOwner());

        verify(itemRepository, times(1)).save(item);
    }

    // Тесты для метода updateItem
    @Test
    void updateItem_ShouldUpdateItem() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item updatedItem = itemService.updateItem(item, user.getId(), item.getId());

        assertNotNull(updatedItem);
        assertEquals(item.getName(), updatedItem.getName());
        assertEquals(item.getDescription(), updatedItem.getDescription());
        assertEquals(item.getAvailable(), updatedItem.getAvailable());
        assertEquals(user, updatedItem.getOwner());

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(item, user.getId(), item.getId()));

        verify(itemRepository, never()).save(any());
    }

    // Тесты для метода getItemWithTimeAndComments
    @Test
    void getItemWithTimeAndComments_ShouldReturnItemDtoWithTime() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.singletonList(comment));
        when(bookingService.getAllBookingsByItem(anyLong())).thenReturn(Collections.emptyList());

        ItemDtoWithTime itemDtoWithTime = itemService.getItemWithTimeAndComments(user.getId(), item.getId());

        assertNotNull(itemDtoWithTime);
        assertEquals(item.getName(), itemDtoWithTime.getName());
        assertEquals(item.getDescription(), itemDtoWithTime.getDescription());
        assertEquals(item.getAvailable(), itemDtoWithTime.getAvailable());
        assertEquals(1, itemDtoWithTime.getComments().size());

        verify(itemRepository, times(1)).findById(item.getId());
        verify(commentRepository, times(1)).findAllByItemId(item.getId());
        verify(bookingService, times(1)).getAllBookingsByItem(item.getId());
    }

    @Test
    void getItemWithTimeAndComments_ShouldSetNullForBookings_WhenUserIsNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.singletonList(comment));
        when(bookingService.getAllBookingsByItem(anyLong())).thenReturn(Collections.emptyList());

        ItemDtoWithTime itemDtoWithTime = itemService.getItemWithTimeAndComments(999L, item.getId());

        assertNull(itemDtoWithTime.getLastBooking());
        assertNull(itemDtoWithTime.getNextBooking());
    }

    // Тесты для метода getItem
    @Test
    void getItem_ShouldReturnItemDto() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.getItem(user.getId(), item.getId());

        assertNotNull(itemDto);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());

        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void getItem_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(user.getId(), item.getId()));
    }

    // Тесты для метода getAllItemsByOwner
    @Test
    void getAllItemsByOwner_ShouldReturnListOfItemDto() {
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        List<ItemDto> itemDtos = itemService.getAllItemsByOwner(user.getId());

        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());
        assertEquals(item.getName(), itemDtos.getFirst().getName());
        assertEquals(item.getDescription(), itemDtos.getFirst().getDescription());
        assertEquals(item.getAvailable(), itemDtos.getFirst().getAvailable());

        verify(itemRepository, times(1)).findAllByOwnerId(user.getId());
    }

    // Тесты для метода searchItems
    @Test
    void searchItems_ShouldReturnListOfItemDto() {
        when(itemRepository.searchItems(anyString())).thenReturn(Collections.singletonList(item));

        List<ItemDto> itemDtos = itemService.searchItems(user.getId(), "search");

        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());
        assertEquals(item.getName(), itemDtos.getFirst().getName());
        assertEquals(item.getDescription(), itemDtos.getFirst().getDescription());
        assertEquals(item.getAvailable(), itemDtos.getFirst().getAvailable());

        verify(itemRepository, times(1)).searchItems("search");
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsBlank() {
        List<ItemDto> itemDtos = itemService.searchItems(user.getId(), "");

        assertNotNull(itemDtos);
        assertTrue(itemDtos.isEmpty());
    }


    @Test
    void addComment_ShouldThrowConflictException_WhenUserHasNoBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingService.getAllBookingsByBooker(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(ConflictException.class, () -> itemService.addComment(comment, user.getId(), item.getId()));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_ShouldThrowConflictException_WhenBookingIsNotFinished() {
        bookingDto.setEnd(LocalDateTime.now().plusDays(1)); // Аренда еще не завершена

        assertThrows(NotFoundException.class, () -> itemService.addComment(comment, user.getId(), item.getId()));

        verify(commentRepository, never()).save(any());
    }

    // Тесты для метода getItemsByRequestId
    @Test
    void getItemsByRequestId_ShouldReturnListOfItems() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.singletonList(item));

        List<Item> items = itemService.getItemsByRequestId(1L);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getName(), items.getFirst().getName());
        assertEquals(item.getDescription(), items.getFirst().getDescription());
        assertEquals(item.getAvailable(), items.getFirst().getAvailable());

        verify(itemRepository, times(1)).findAllByRequestId(1L);
    }
}