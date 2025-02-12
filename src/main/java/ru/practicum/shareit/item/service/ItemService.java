package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public Item addItem(ItemDto itemDto, Long userId);

    public Item updateItem(ItemDto itemDto, Long userId, Long itemId);

    public ItemDto getItem(Long userId, Long itemId);

    public List<ItemDto> getAllItemsByOwner(Long userId);

    public List<ItemDto> searchItems(Long userId, String text);
}
