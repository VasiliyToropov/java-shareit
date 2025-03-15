package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final String headerName = "X-Sharer-User-Id";

    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item addItem(@RequestBody Item item, @RequestHeader(headerName) Optional<Long> optionalUserId) {
        return itemService.addItem(item, optionalUserId);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@RequestBody Comment comment, @RequestHeader(headerName) Optional<Long> optionalUserId, @PathVariable Long itemId) {
        return itemService.addComment(comment, optionalUserId, itemId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody Item item, @RequestHeader(headerName) Optional<Long> optionalUserId, @PathVariable Long itemId) {
        return itemService.updateItem(item, optionalUserId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithTime getItem(@RequestHeader(headerName) Optional<Long> optionalUserId, @PathVariable Long itemId) {
        return itemService.getItemWithTimeAndComments(optionalUserId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader(headerName) Optional<Long> optionalUserId) {
        return itemService.getAllItemsByOwner(optionalUserId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(headerName) Optional<Long> optionalUserId, @RequestParam("text") String text) {
        return itemService.searchItems(optionalUserId, text);
    }


}
