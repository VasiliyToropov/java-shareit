package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public Booking addBooking(@RequestBody Booking booking, @RequestHeader(headerName) Optional<Long> optionalUserId) {
        return bookingService.addBooking(booking, optionalUserId);
    }

    @PatchMapping("/{bookingId}")
    public Booking isApprovedBooking(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                     @RequestHeader(headerName) Optional<Long> optionalUserId) {
        return bookingService.isApprovedBooking(bookingId, approved, optionalUserId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping(params = "state")
    public List<BookingDto> getAllBookingsByBooker(@RequestParam String state, @RequestHeader(headerName) Optional<Long> optionalBookerId) {
        return bookingService.getAllBookingsByBooker(state, optionalBookerId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestHeader(headerName) Optional<Long> optionalBookerId) {
        return bookingService.getAllBookingsByBooker(optionalBookerId);
    }

    @GetMapping(path = "/owner", params = "state")
    public List<BookingDto> getAllBookingsByOwner(@RequestParam String state, @RequestHeader(headerName) Optional<Long> optionalOwnerId) {
        return bookingService.getAllBookingsByOwner(state, optionalOwnerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader(headerName) Optional<Long> optionalOwnerId) {
        return bookingService.getAllBookingsByOwner(optionalOwnerId);
    }
}
