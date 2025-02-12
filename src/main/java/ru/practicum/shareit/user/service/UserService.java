package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface UserService {

    public User getUser(Long id);

    public List<UserDto> getUsersDto();

    public User createUser(UserDto user);

    public User updateUser(UserDto user, Long id);

    public void deleteUser(Long id);
}
