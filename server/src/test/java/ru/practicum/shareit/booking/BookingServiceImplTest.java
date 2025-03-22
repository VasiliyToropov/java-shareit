package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddBooking_Success() {
        Long userId = 1L;
        Long itemId = 1L;
        Booking booking = new Booking();
        booking.setItemId(itemId);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));

        User booker = new User();
        booker.setId(userId);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(true);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);

        when(userService.getUser(userId)).thenReturn(booker);
        when(itemService.getItem(userId, itemId)).thenReturn(itemDto);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking addedBooking = bookingService.addBooking(booking, userId);

        assertNotNull(addedBooking);
        assertEquals(Booking.Status.WAITING, addedBooking.getStatus());
        assertEquals(booker, addedBooking.getBooker());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testAddBooking_ItemNotAvailable() {
        Long userId = 1L;
        Long itemId = 1L;
        Booking booking = new Booking();
        booking.setItemId(itemId);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setAvailable(false);

        when(itemService.getItem(userId, itemId)).thenReturn(itemDto);

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(booking, userId));
    }

    @Test
    public void testIsApprovedBooking_Approved() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(Booking.Status.WAITING);

        Item item = new Item();
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);

        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking updatedBooking = bookingService.isApprovedBooking(bookingId, approved, userId);

        assertNotNull(updatedBooking);
        assertEquals(Booking.Status.APPROVED, updatedBooking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testIsApprovedBooking_Rejected() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = false;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(Booking.Status.WAITING);

        Item item = new Item();
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);

        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking updatedBooking = bookingService.isApprovedBooking(bookingId, approved, userId);

        assertNotNull(updatedBooking);
        assertEquals(Booking.Status.REJECTED, updatedBooking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testIsApprovedBooking_NotFound() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.isApprovedBooking(bookingId, approved, userId));
    }

    @Test
    public void testIsApprovedBooking_OwnerMismatch() {
        Long bookingId = 1L;
        Long userId = 1L;
        Long otherUserId = 2L;
        Boolean approved = true;

        Booking booking = new Booking();
        booking.setId(bookingId);

        Item item = new Item();
        User owner = new User();
        owner.setId(otherUserId);
        item.setOwner(owner);

        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.isApprovedBooking(bookingId, approved, userId));
    }

    @Test
    public void testGetBookingById_Success() {
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBookingById(bookingId);

        assertNotNull(bookingDto);
        assertEquals(bookingId, bookingDto.getId());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    public void testGetBookingById_NotFound() {
        Long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId));
    }


    @Test
    public void testGetAllBookingsByBooker_InvalidState() {
        Long bookerId = 1L;
        String invalidState = "INVALID";

        assertThrows(BadRequestException.class, () -> bookingService.getAllBookingsByBooker(invalidState, bookerId));
    }


    @Test
    public void testGetAllBookingsByOwner_InvalidState() {
        Long ownerId = 1L;
        String invalidState = "INVALID";

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User());

        when(itemService.getAllItemsByOwnerId(ownerId)).thenReturn(Collections.singletonList(item));

        assertThrows(BadRequestException.class, () -> bookingService.getAllBookingsByOwner(invalidState, ownerId));
    }

    @Test
    public void testGetAllBookingsByItem() {
        Long itemId = 1L;
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingRepository.findByItemId(itemId)).thenReturn(Collections.singletonList(booking));

        List<Booking> bookings = bookingService.getAllBookingsByItem(itemId);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByItemId(itemId);
    }
}