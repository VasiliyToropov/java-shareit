package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.Map;
import java.util.Optional;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addBooking(BookItemRequestDto booking, Optional<Long> optionalUserId) {
        Long userId = optionalUserId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return post("", userId, booking);
    }

    public ResponseEntity<Object> isApprovedBooking(Long bookingId, Boolean approved, Optional<Long> optionalUserId) {
        Long userId = optionalUserId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Map<String, Object> parameters = Map.of(
                "approved", approved);

        return patch("/" + bookingId + "?approved=" + approved, userId, parameters);
    }

    public ResponseEntity<Object> getBookingById(Long bookingId) {
        return get("/" + bookingId);
    }


    public ResponseEntity<Object> getAllBookingsByBooker(String state, Optional<Long> optionalBookerId) {
        Long bookerId = optionalBookerId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Map<String, Object> parameters = Map.of(
                "state", state);

        return get("", bookerId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByBooker(Optional<Long> optionalBookerId) {
        Long bookerId = optionalBookerId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return get("", bookerId);
    }

    public ResponseEntity<Object> getAllBookingsByOwner(String state, Optional<Long> optionalOwnerId) {
        Long ownerId = optionalOwnerId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Map<String, Object> parameters = Map.of(
                "state", state);

        return get("/owner?state=" + state, ownerId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByOwner(Optional<Long> optionalOwnerId) {
        Long ownerId = optionalOwnerId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return get("/owner", ownerId);
    }
}
