package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public Booking addBooking(@RequestBody Booking booking, @RequestHeader(headerName) Long userId) {
        return bookingService.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking isApprovedBooking(@PathVariable Long bookingId, @RequestParam Boolean approved, @RequestHeader(headerName) Long userId) {
        return bookingService.isApprovedBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping(params = "state")
    public List<BookingDto> getAllBookingsByBooker(@RequestParam String state, @RequestHeader(headerName) Long bookerId) {
        return bookingService.getAllBookingsByBooker(state, bookerId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestHeader(headerName) Long bookerId) {
        return bookingService.getAllBookingsByBooker(bookerId);
    }

    @GetMapping(path = "/owner", params = "state")
    public List<BookingDto> getAllBookingsByOwner(@RequestParam String state, @RequestHeader(headerName) Long ownerId) {
        return bookingService.getAllBookingsByOwner(state, ownerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader(headerName) Long ownerId) {
        return bookingService.getAllBookingsByOwner(ownerId);
    }
}
