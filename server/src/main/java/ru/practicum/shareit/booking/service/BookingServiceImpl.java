package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Qualifier("BookingServiceImpl")
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public Booking addBooking(Booking booking, Long userId) {


        Booking addedBooking = new Booking();
        User booker = userService.getUser(userId);
        ItemDto itemDto = itemService.getItem(userId, booking.getItemId());
        Item item = ItemMapper.INSTANCE.toItem(itemDto);

        //Проверяем, что предмет доступен
        if (!item.getAvailable()) {
            throw new BadRequestException("Выбранный предмет не доступен");
        }

        addedBooking.setBooker(booker);
        addedBooking.setItem(item);
        addedBooking.setStatus(Booking.Status.WAITING);
        addedBooking.setStart(booking.getStart());
        addedBooking.setEnd(booking.getEnd());
        addedBooking.setItemId(booking.getItemId());

        bookingRepository.save(addedBooking);

        log.info("Добавлено бронирование для пользователя {}", userId);

        return addedBooking;
    }

    @Override
    public Booking isApprovedBooking(Long bookingId, Boolean approved, Long userId) {
        Booking updatedBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с таким ID не найдено"));

        Item item = updatedBooking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Id владельца не совпадают");
        }

        if (approved) {
            updatedBooking.setStatus(Booking.Status.APPROVED);
            log.info("Бронирование разрешено");
        } else {
            updatedBooking.setStatus(Booking.Status.REJECTED);
            log.info("Бронирование запрещено");
        }

        bookingRepository.save(updatedBooking);

        return updatedBooking;
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с таким ID не найдено"));

        log.info("Получена информация о бронировании с id: {}", bookingId);

        return BookingMapper.INSTANCE.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(String state, Long bookerId) {

        log.info("Получение списка бронирования для арендующего пользователя с учетом состояния бронирования");

        Instant now = Instant.now();

        return switch (state) {
            case "ALL" -> BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByBookerId(bookerId));
            case "CURRENT" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByBookerWithCurrentState(bookerId, now));
            case "PAST" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByBookerWithPastState(bookerId, now));
            case "FUTURE" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByBookerWithFutureState(bookerId, now));
            case "WAITING" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByBookerWithWaitingState(bookerId));
            case "REJECTED" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByBookerWithRejectedState(bookerId));
            default -> throw new BadRequestException("Неверное состояние запроса на получение аренды");
        };
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(Long bookerId) {

        log.info("Получение списка бронирования для арендующего пользователя");

        return BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByBookerId(bookerId));
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(Long ownerId) {

        //Сначала надо найти вещи пользователя
        List<Item> items = itemService.getAllItemsByOwnerId(ownerId);

        if (items.isEmpty()) {
            throw new NotFoundException("Для этого пользователя не найдены вещи");
        }

        //Оставляем только список Id вещей
        Set<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toSet());

        log.info("Получение списка бронирования хозяина вещей");

        return BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByOwnerId(itemsId));
    }

    @Override
    public List<Booking> getAllBookingsByItem(Long itemId) {

        log.info("Получение списка бронирования для определенной вещи");

        return bookingRepository.findByItemId(itemId);
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(String state, Long ownerId) {

        Instant now = Instant.now();

        //Сначала надо найти вещи пользователя
        List<Item> items = itemService.getAllItemsByOwnerId(ownerId);

        if (items.isEmpty()) {
            throw new ConflictException("Для этого пользователя не найдены вещи");
        }

        //Оставляем только список Id вещей
        Set<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toSet());

        log.info("Получение списка бронирования для определенной вещи с учетом состояния");

        return switch (state) {
            case "ALL" -> BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByOwnerId(itemsId));
            case "CURRENT" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByOwnerWithCurrentState(itemsId, now));
            case "PAST" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByOwnerWithPastState(itemsId, now));
            case "FUTURE" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByOwnerWithFutureState(itemsId, now));
            case "WAITING" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByOwnerWithWaitingState(itemsId));
            case "REJECTED" ->
                    BookingMapper.INSTANCE.toBooksDto(bookingRepository.findAllByOwnerWithRejectedState(itemsId));
            default -> throw new BadRequestException("Неверное состояние запроса на получение аренды");
        };
    }
}
