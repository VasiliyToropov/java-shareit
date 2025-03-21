package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.ConflictException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Vasiliy Toropov");

        item = new Item();
        item.setId(1L);
        item.setName("Андронный коллайдер");
        item.setDescription("Простой обычный советский..");
        item.setAvailable(true);
        item.setOwner(user);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Физикам пригодится");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthorName(user.getName());
        comment.setItem(item);
    }

    @Test
    void addItemTest() {
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

    @Test
    void updateItemTest() {
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
    void getItemWithTimeAndCommentsTest() {
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
    void addCommentTest() {

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingService.getAllBookingsByBooker(anyLong())).thenReturn(Collections.emptyList());


        assertThrows(ConflictException.class, () -> itemService.addComment(comment, user.getId(), item.getId()));

        verify(commentRepository, never()).save(comment);
    }

    @Test
    void searchItemsTest() {
        when(itemRepository.searchItems(anyString())).thenReturn(Collections.singletonList(item));

        List<ItemDto> itemDtos = itemService.searchItems(user.getId(), "search");

        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());
        assertEquals(item.getName(), itemDtos.getFirst().getName());
        assertEquals(item.getDescription(), itemDtos.getFirst().getDescription());
        assertEquals(item.getAvailable(), itemDtos.getFirst().getAvailable());

        verify(itemRepository, times(1)).searchItems("search");
    }
}
