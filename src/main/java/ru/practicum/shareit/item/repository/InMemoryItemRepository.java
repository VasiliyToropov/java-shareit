package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("InMemoryItemRepository")
@Slf4j
public class InMemoryItemRepository implements ItemRepository {

    private final List<Item> items = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Item addItem(ItemDto itemDto, User user) {
        Item addedItem = new Item();

        addedItem.setId(id);
        addedItem.setName(itemDto.getName());
        addedItem.setDescription(itemDto.getDescription());
        addedItem.setOwner(user);
        addedItem.setItemRequest(null);
        addedItem.setAvailable(itemDto.getAvailable());

        id++;

        ItemDto addedItemDto = ItemMapper.toItemDto(addedItem);

        user.getItems().add(addedItemDto);

        items.add(addedItem);

        log.info("Добавлен предмет с id: {}", id);

        return addedItem;
    }

    @Override
    public Item updateItem(ItemDto itemDto, User user, Long itemId) {
        Item updatedItem = new Item();
        Item deletedItem = items.get(itemId.intValue());

        updatedItem.setId(itemId);
        updatedItem.setName(itemDto.getName());
        updatedItem.setDescription(itemDto.getDescription());
        updatedItem.setOwner(user);
        updatedItem.setItemRequest(null);
        updatedItem.setAvailable(itemDto.getAvailable());

        items.remove(deletedItem);
        items.add(updatedItem);

        log.info("Обновили предмет с id: {}", itemId);

        return updatedItem;
    }

    @Override
    public ItemDto getItem(User user, Long itemId) {
        Item item = items.get(itemId.intValue());

        log.info("Получили предмет с id: {}", itemId);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(User user) {

        log.info("Получили список всех предметов от пользователя с id: {}", user.getId());

        return user.getItems();
    }

    @Override
    public List<ItemDto> searchItems(User user, String text) {

        List<ItemDto> foundedItems = new ArrayList<>();
        List<ItemDto> items = getAllItemsByOwner(user);

        //Если текст поиска пустой или null возвращаем пустой массив
        if (text == null || text.isBlank()) {
            return foundedItems;
        }

        foundedItems = items.stream().filter(i -> (i.getName().toUpperCase().contains(text) ||
                i.getDescription().toUpperCase().contains(text)) && i.getAvailable()).toList();

        log.info("Получили результаты поиска для запроса: {}", text);

        return foundedItems;
    }

}
