package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    public Booking addBooking(Booking booking, Long userId);

    public Booking isApprovedBooking(Long bookingId, Boolean approved, Long userId);

    public BookingDto getBookingById(Long bookingId);

    public List<BookingDto> getAllBookingsByBooker(String state, Long bookerId);

    public List<BookingDto> getAllBookingsByOwner(String state, Long ownerId);

    public List<BookingDto> getAllBookingsByBooker(Long bookerId);

    public List<BookingDto> getAllBookingsByOwner(Long ownerId);

    public List<Booking> getAllBookingsByItem(Long itemId);
}
