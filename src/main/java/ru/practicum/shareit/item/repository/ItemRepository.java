package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    public Item addItem(ItemDto itemDto, User user);

    public Item updateItem(ItemDto itemDto, User user, Long itemId);

    public ItemDto getItem(User user, Long itemId);

    public List<ItemDto> getAllItemsByOwner(User user);

    public List<ItemDto> searchItems(User user, String text);
}
