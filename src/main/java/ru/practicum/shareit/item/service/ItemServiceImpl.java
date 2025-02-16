package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(@Qualifier("InMemoryItemRepository") ItemRepository itemRepository,
                           @Qualifier("InMemoryUserRepository") UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Item addItem(ItemDto itemDto, Long userId) {
        User user = getUserById(userId);

        if (itemDto.getName().isEmpty() || itemDto.getAvailable() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Поля имя, описание и доступность не должны быть null");
        }

        return itemRepository.addItem(itemDto, user);
    }

    @Override
    public Item updateItem(ItemDto itemDto, Long userId, Long itemId) {
        User user = getUserById(userId);
        return itemRepository.updateItem(itemDto, user, itemId);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        User user = getUserById(userId);
        return itemRepository.getItem(user, itemId);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        User user = getUserById(userId);
        return itemRepository.getAllItemsByOwner(user);
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        User user = getUserById(userId);
        return itemRepository.searchItems(user, text);
    }

    private User getUserById(Long id) {

        User foundedUser = userRepository.getUser(id);

        if (foundedUser == null) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        return foundedUser;
    }
}
