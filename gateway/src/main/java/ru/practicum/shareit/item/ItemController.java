package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDto item, @RequestHeader(headerName) Optional<Long> optionalUserId) {
        return itemClient.addItem(item, optionalUserId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentDto comment, @RequestHeader(headerName) Optional<Long> optionalUserId, @PathVariable Long itemId) {
        return itemClient.addComment(comment, optionalUserId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid ItemDto item, @RequestHeader(headerName) Optional<Long> optionalUserId, @PathVariable Long itemId) {
        return itemClient.updateItem(item, optionalUserId, itemId);
    }

    //itemDtoWithTime
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(headerName) Optional<Long> optionalUserId, @PathVariable Long itemId) {
        return itemClient.getItemWithTimeAndComments(optionalUserId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader(headerName) Optional<Long> optionalUserId) {
        return itemClient.getAllItemsByOwner(optionalUserId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(headerName) Optional<Long> optionalUserId, @RequestParam("text") String text) {
        return itemClient.searchItems(optionalUserId, text);
    }
}
