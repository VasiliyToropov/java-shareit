package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Qualifier("UserServiceImpl")

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(@Qualifier("InMemoryUserRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(Long id) {
        return userRepository.getUser(id);
    }

    @Override
    public List<UserDto> getUsersDto() {
        return userRepository.getUsersDto();
    }

    @Override
    public User createUser(UserDto user) {

        if (user.getName() == null || user.getEmail() == null) {
            throw new ValidationException("Имя или почта пользователя не должны быть null");
        }

        checkConflicts(user);

        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(UserDto user, Long id) {
        checkConflicts(user);
        return userRepository.updateUser(user, id);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    public void checkConflicts(UserDto userDto) {

        // Проверяем одинаковые Email
        String checkedEmail = userDto.getEmail();

        List<User> users = userRepository.getUsers();

        boolean isConflicted = users.stream().anyMatch(u -> u.getEmail().equals(checkedEmail));

        if (isConflicted) {
            throw new ConflictException("Email пользователя совпадает с существующим");
        }
    }
}

