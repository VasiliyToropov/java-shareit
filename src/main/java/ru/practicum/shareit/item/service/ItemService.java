package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    public Item addItem(Item item, Optional<Long> optionalUserId);

    public Item updateItem(Item item, Optional<Long> optionalUserId, Long itemId);

    public ItemDto getItem(Optional<Long> optionalUserId, Long itemId);

    public List<ItemDto> getAllItemsByOwner(Optional<Long> userId);

    public List<ItemDto> searchItems(Optional<Long> userId, String text);

    public List<Item> getAllItemsByOwnerId(Optional<Long> ownerId);

    public Comment addComment(Comment comment, Optional<Long> userId, Long itemId);

    public ItemDtoWithTime getItemWithTimeAndComments(Optional<Long> userId, Long itemId);
}
