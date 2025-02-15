package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("InMemoryUserRepository")
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private Long id = 0L;
    private final List<User> users = new ArrayList<>();

    @Override
    public User getUser(Long id) {

        return getUserAndCheckId(id);
    }

    @Override
    public List<UserDto> getUsersDto() {
        List<UserDto> dtoUsers = new ArrayList<>();

        for (User user : users) {
            dtoUsers.add(UserMapper.INSTANCE.toUserDto(user));
        }

        return dtoUsers;
    }

    @Override
    public List<User> getUsers() {
        log.info("Получили список всех пользователей");

        return users;
    }

    @Override
    public User createUser(UserDto userDto) {

        User addedUser = new User();
        addedUser.setId(id);
        addedUser.setName(userDto.getName());
        addedUser.setEmail(userDto.getEmail());

        users.add(addedUser);

        log.info("Создан пользователь с id: {}", id);

        id++;

        return addedUser;
    }

    @Override
    public User updateUser(UserDto user, Long id) {

        User deletedUser = getUserAndCheckId(id);
        User addedUser = new User();

        // Если на входе указано только имя
        if (user.getEmail() == null) {
            addedUser.setId(deletedUser.getId());
            addedUser.setName(user.getName());
            addedUser.setEmail(deletedUser.getEmail());
        }

        // Если на входе указана только почта
        if (user.getName() == null) {
            addedUser.setId(deletedUser.getId());
            addedUser.setName(deletedUser.getName());
            addedUser.setEmail(user.getEmail());
        }

        //Если на входе указано имя и почта
        if (user.getName() != null && user.getEmail() != null) {
            addedUser.setId(deletedUser.getId());
            addedUser.setName(user.getName());
            addedUser.setEmail(user.getEmail());
        }

        users.remove(deletedUser);
        users.add(addedUser);

        log.info("Обновили пользователя с id: {}", id);

        return addedUser;
    }

    @Override
    public void deleteUser(Long id) {
        User deletedUser = getUserAndCheckId(id);

        users.remove(deletedUser);

        log.info("Удалили пользователя с id: {}", id);
    }

    public User getUserAndCheckId(Long id) {
        Optional<User> user = users.stream().filter(u -> u.getId().equals(id)).findFirst();
        User foundedUser = user.orElse(null);

        if (foundedUser == null) {
            throw new NotFoundException("Пользователь с таким Id не найден");
        }

        return foundedUser;
    }

}
