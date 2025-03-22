package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.Optional;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody @Valid RequestDto itemRequest, @RequestHeader(headerName) Optional<Long> optionalUserId) {
        return requestClient.addItemRequest(itemRequest, optionalUserId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader(headerName) Optional<Long> optionalUserId) {
        return requestClient.getItemRequestsByUserId(optionalUserId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(headerName) Optional<Long> optionalUserId) {
        return requestClient.getAllItemRequests(optionalUserId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestsByRequestId(@PathVariable Long requestId, @RequestHeader(headerName) Optional<Long> optionalUserId) {
        return requestClient.getItemRequestsByRequestId(requestId, optionalUserId);
    }
}
