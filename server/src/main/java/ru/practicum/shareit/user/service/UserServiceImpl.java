package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Qualifier("UserServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);

        log.info("Получили пользователя с id: {}", id);

        return user.orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }

    @Override
    public List<UserDto> getUsersDto() {
        List<User> users = userRepository.findAll();

        log.info("Получили список всех пользователей");

        return UserMapper.INSTANCE.toUserDtoList(users);
    }

    @Override
    public User createUser(UserDto user) {

        if (user.getName() == null || user.getEmail() == null) {
            throw new ValidationException("Имя или почта пользователя не должны быть null");
        }

        checkConflicts(user);

        User addedUser = UserMapper.INSTANCE.toUser(user);

        log.info("Добавили нового пользователя");

        return userRepository.save(addedUser);
    }

    @Override
    public User updateUser(UserDto user, Long id) {
        checkConflicts(user);

        User updatedUser = getUser(id);

        // Если на входе указано только имя
        if (user.getEmail() == null) {
            updatedUser.setName(user.getName());
        }

        // Если на входе указана только почта
        if (user.getName() == null) {
            updatedUser.setEmail(user.getEmail());
        }

        //Если на входе указано имя и почта
        if (user.getName() != null && user.getEmail() != null) {
            updatedUser.setName(user.getName());
            updatedUser.setEmail(user.getEmail());
        }

        userRepository.save(updatedUser);

        log.info("Обновили пользователя с id: {}", id);

        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удалили пользователя с id: {}", id);
        userRepository.deleteById(id);
    }

    public void checkConflicts(UserDto userDto) {

        // Проверяем одинаковые Email
        String checkedEmail = userDto.getEmail();

        List<User> users = userRepository.findAll();

        boolean isConflicted = users.stream().anyMatch(u -> u.getEmail().equals(checkedEmail));

        if (isConflicted) {
            throw new ConflictException("Email пользователя совпадает с существующим");
        }
    }
}

