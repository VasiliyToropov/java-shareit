package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("ItemServiceImpl")
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService,
                           CommentRepository commentRepository, @Lazy BookingService bookingService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
    }

    @Override
    public Item addItem(Item item, Long userId) {
        User user = getUserById(userId);

        if (item.getName().isEmpty() || item.getAvailable() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Поля имя, описание и доступность не должны быть null");
        }

        item.setOwner(user);

        log.info("Добавили вещь в БД");

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item, Long userId, Long itemId) {
        User user = getUserById(userId);
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с таким id найден"));

        updatedItem.setOwner(user);
        updatedItem.setDescription(item.getDescription());
        updatedItem.setName(item.getName());
        updatedItem.setAvailable(item.getAvailable());

        log.info("Обновили вещь с id: {}", itemId);

        return itemRepository.save(updatedItem);
    }

    @Override
    public ItemDtoWithTime getItemWithTimeAndComments(Long userId, Long itemId) {

        List<Comment> comments = getComments(itemId);

        List<Booking> bookingsForItem = bookingService.getAllBookingsByItem(itemId);

        LocalDateTime now = LocalDateTime.now();

        //Находим время последнего бронирования
        Optional<LocalDateTime> lastBookingForItem = bookingsForItem.stream()
                .map(Booking::getEnd)
                .filter(end -> end.isBefore(now))
                .max(LocalDateTime::compareTo);

        // Находим время ближайшего будущего бронирования
        Optional<LocalDateTime> nextBookingForItem = bookingsForItem.stream()
                .map(Booking::getStart)
                .filter(start -> start.isAfter(now))
                .min(LocalDateTime::compareTo);

        LocalDateTime lastBooking = lastBookingForItem.orElse(null);
        LocalDateTime nextBooking = nextBookingForItem.orElse(null);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с таким id найден"));
        ItemDtoWithTime itemDto = new ItemDtoWithTime();

        //Проверяем что пользователь является владельцем вещи
        if (!item.getOwner().getId().equals(userId)) {
            lastBooking = null;
        }

        itemDto.setName(item.getName());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setDescription(item.getDescription());
        itemDto.setId(item.getId());
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments);

        log.info("Получили вещь с id: {} вместе с комментариями и датами бронирования", itemId);

        return itemDto;
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с таким id найден"));

        log.info("Получили вещь с id: {}", itemId);

        return ItemMapper.INSTANCE.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        log.info("Получили список вещей, принадлежащему пользователю с id: {}", userId);

        return ItemMapper.INSTANCE.toListDto(items);
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {

        List<ItemDto> foundedDtoItems = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return foundedDtoItems;
        }

        List<Item> foundedItems = itemRepository.searchItems(text);

        foundedDtoItems = ItemMapper.INSTANCE.toListDto(foundedItems);

        log.info("Получили список вещей для поискового запроса: {}", text);

        return foundedDtoItems;
    }

    private User getUserById(Long id) {
        return userService.getUser(id);
    }

    //Для BookingService требуется список предметов
    @Override
    public List<Item> getAllItemsByOwnerId(Long ownerId) {

        log.info("Получен список вещей, принадлежащих пользователю с id: {}", ownerId);

        return itemRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Comment addComment(Comment comment, Long userId, Long itemId) {

        Item item = ItemMapper.INSTANCE.toItem(getItem(userId, itemId));
        LocalDateTime localDateTime = LocalDateTime.now();

        //Проверяем, что пользователь брал вещь в аренду
        List<BookingDto> bookings = bookingService.getAllBookingsByBooker(userId);

        if (bookings.isEmpty()) {
            throw new NotFoundException("Пользователь не брал вещей в аренду");
        }

        //Проверяем, что пользователь может оставлять комментарий только после окончания аренды
        for (BookingDto booking : bookings) {
            if (booking.getEnd().isAfter(localDateTime)) {
                throw new ConflictException("Пользователь может оставлять комментарий только после срока окончания аренды");
            }
        }

        Comment addedComment = new Comment();

        addedComment.setText(comment.getText());
        addedComment.setItem(item);
        addedComment.setAuthorName(getUserById(userId).getName());
        addedComment.setCreated(localDateTime);

        log.info("Добавлен комментарий для вещи с id: {} от пользователя с id: {}", itemId, userId);

        return commentRepository.save(addedComment);
    }

    public List<Comment> getComments(Long itemId) {

        log.info("Получение списка всех комментариев для вещи с id: {}", itemId);

        return commentRepository.findAllByItemId(itemId);
    }
}
