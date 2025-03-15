package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    public Booking addBooking(Booking booking, Optional<Long> optionalUserId);

    public Booking isApprovedBooking(Long bookingId, Boolean approved, Optional<Long> optionalUserId);

    public BookingDto getBookingById(Long bookingId);

    public List<BookingDto> getAllBookingsByBooker(String state, Optional<Long> optionalBookerId);

    public List<BookingDto> getAllBookingsByOwner(String state, Optional<Long> optionalOwnerId);

    public List<BookingDto> getAllBookingsByBooker(Optional<Long> optionalBookerId);

    public List<BookingDto> getAllBookingsByOwner(Optional<Long> optionalOwnerId);

    public List<Booking> getAllBookingsByItem(Long itemId);
}
