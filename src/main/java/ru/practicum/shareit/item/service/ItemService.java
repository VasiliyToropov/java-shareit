package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public Item addItem(Item item, Long userId);

    public Item updateItem(Item item, Long userId, Long itemId);

    public ItemDto getItem(Long userId, Long itemId);

    public List<ItemDto> getAllItemsByOwner(Long userId);

    public List<ItemDto> searchItems(Long userId, String text);

    public List<Item> getAllItemsByOwnerId(Long ownerId);

    public Comment addComment(Comment comment, Long userId, Long itemId);

    public ItemDtoWithTime getItemWithTimeAndComments(Long userId, Long itemId);
}
