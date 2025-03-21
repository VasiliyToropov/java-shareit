package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.util.Optional;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Valid BookItemRequestDto booking, @RequestHeader(headerName) Optional<Long> optionalUserId) {
        return bookingClient.addBooking(booking, optionalUserId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> isApprovedBooking(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                                    @RequestHeader(headerName) Optional<Long> optionalUserId) {
        return bookingClient.isApprovedBooking(bookingId, approved, optionalUserId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId) {
        return bookingClient.getBookingById(bookingId);
    }

    @GetMapping(params = "state")
    public ResponseEntity<Object> getAllBookingsByBooker(@RequestParam String state, @RequestHeader(headerName) Optional<Long> optionalBookerId) {
        return bookingClient.getAllBookingsByBooker(state, optionalBookerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBooker(@RequestHeader(headerName) Optional<Long> optionalBookerId) {
        return bookingClient.getAllBookingsByBooker(optionalBookerId);
    }

    @GetMapping(path = "/owner", params = "state")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestParam String state, @RequestHeader(headerName) Optional<Long> optionalOwnerId) {
        return bookingClient.getAllBookingsByOwner(state, optionalOwnerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader(headerName) Optional<Long> optionalOwnerId) {
        return bookingClient.getAllBookingsByOwner(optionalOwnerId);
    }
}
